package ru.anna.wordmemorizer;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.anna.wordmemorizer.components.Buttons;
import ru.anna.wordmemorizer.components.ExerciseType;
import ru.anna.wordmemorizer.components.StatisticsService;
import ru.anna.wordmemorizer.components.TopicService;
import ru.anna.wordmemorizer.config.BotConfig;
import ru.anna.wordmemorizer.entity.*;
import ru.anna.wordmemorizer.repository.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.anna.wordmemorizer.components.BotCommands.*;
import static ru.anna.wordmemorizer.components.ExerciseType.FOREIGN_RUSSIAN;
import static ru.anna.wordmemorizer.components.ExerciseType.RUSSIAN_FOREIGN;

@Slf4j
@Component
public class WordMemorizerBot extends TelegramLongPollingBot {
    private final BotConfig config;

    private final ForeignWordRepository foreignWordRepository;

    private final RussianWordRepository russianWordRepository;

    private final UserRepository userRepository;

    private final BotActivityRepository botActivityRepository;

    private final UserReplyRepository userReplyRepository;

    private final TopicService topicService;

    private final StatisticsService statisticsService;

    private static final String THEME_PREFIX = "/t";
    private static final String THEME_PREFIX_FOREIGN = "/tf_";
    private static final String THEME_PREFIX_RUSSIAN = "/tr_";
    public static final String STATISTICS_PREFIX = "/s_";

    private static final Random random = new Random();

    public WordMemorizerBot(BotConfig config, ForeignWordRepository foreignWordRepository, RussianWordRepository russianWordRepository, UserRepository userRepository, BotActivityRepository botActivityRepository, UserReplyRepository userReplyRepository, TopicService topicService, StatisticsService statisticsService) {
        this.config = config;
        this.userRepository = userRepository;
        this.botActivityRepository = botActivityRepository;
        this.userReplyRepository = userReplyRepository;
        this.topicService = topicService;
        this.statisticsService = statisticsService;
        try {
            this.execute(new SetMyCommands(LIST_OF_COMMANDS, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e){
            log.error(e.getMessage());
        }
        this.foreignWordRepository = foreignWordRepository;
        this.russianWordRepository = russianWordRepository;
    }
    @Override
    public String getBotUsername() { return config.getBotName(); }

    @Override
    public String getBotToken() { return config.getToken(); }

    @Override
    public void onUpdateReceived(@NotNull Update update) {
        long chatId;
        String userName;
        String receivedMessage;

        //если получено сообщение текстом
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
            userName = update.getMessage().getFrom().getFirstName();

            if (update.getMessage().hasText()) {
                receivedMessage = update.getMessage().getText();
                botAnswerUtils(receivedMessage, chatId, userName, getUserIdByChatId(chatId, userName));
            }

            //если нажата одна из кнопок бота
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            userName = update.getCallbackQuery().getFrom().getFirstName();
            receivedMessage = update.getCallbackQuery().getData();

            // find/create User, write his message to DB

            botAnswerUtils(receivedMessage, chatId, userName, getUserIdByChatId(chatId, userName));
        }
    }

    private void botAnswerUtils(String receivedMessage, long chatId, String userName, Long userId) {
        switch (receivedMessage) {
            case START_COMMAND -> sendTextWithAction(chatId, Buttons.inlineMarkup(),
                    "Hi, " + userName + "! I'm a Telegram bot. I can help you to memorize translation of foreign words.");
            case HELP_COMMAND -> sendText(chatId, HELP_TEXT);
            case CHOOSE_ENG_THEME_COMMAND -> sendTextWithAction(chatId, topicService.chooseTheme(Topic::getForeignTopic, THEME_PREFIX_FOREIGN),
                    "Please choose topic to learn");
            case CHOOSE_RUS_THEME_COMMAND-> sendTextWithAction(chatId, topicService.chooseTheme(Topic::getTranslation, THEME_PREFIX_RUSSIAN),
                    "Please choose topic to learn");
            case SKIP_COMMAND -> sendNextWord(chatId, true, false);
            case SHOW_ANSWER_COMMAND -> sendNextWord(chatId, false, true);
            case SHOW_STATISTICS_COMMAND -> sendTextWithAction(chatId, statisticsService.chooseTheme(),
                    "Please choose theme to show statistics for");

            default -> {

                if (receivedMessage.startsWith(THEME_PREFIX)) {
                    //save
                    UsersReply reply = new UsersReply();
                    BotActivity lastBotActivity = botActivityRepository.getLastBotActivityByUserId(userId);
                    User user = new User();
                    user.setId(userRepository.getUserIdByChatId(chatId));
                    reply.setUser(user);
                    reply.setExerciseType(receivedMessage.startsWith(THEME_PREFIX_FOREIGN) ? 1 : 2);
                    reply.setReply(receivedMessage);
                    reply.setBotActivity(lastBotActivity);
                    userReplyRepository.save(reply);
                    checkThemeAndSendWord(receivedMessage, chatId);
                    break;
                }

                if (receivedMessage.startsWith(STATISTICS_PREFIX)) {
                    String statText = statisticsService.getStatistics(userId, Long.valueOf(receivedMessage.substring(3)));
                    sendText(chatId, statText);
                    break;
                }

                // user send translation of previously sent word
                if (checkTranslation(receivedMessage, chatId)) {
                    sendText(chatId, "You're right!");
                    sendNextWord(chatId, false, false);
                } else {
                    sendTextWithAction(chatId, Buttons.exerciseInlineMarkup(), "You're wrong, please try again");
                }
            }
        }
    }

    private void sendNextWord(long chatId, boolean skipWord, boolean showAnswer) {
        Long userId = getUserIdByChatId(chatId, null);
        ExerciseType type = ExerciseType.getExerciseTypeById(userReplyRepository.getExerciseTypeByUserId(userId));
        BotActivity lastBotActivity = botActivityRepository.getLastBotActivityByUserId(userId);
        BotActivity botActivity = new BotActivity();

        if (skipWord) {
            UsersReply reply = new UsersReply();
            User user = new User();
            user.setId(userId);
            reply.setUser(user);
            reply.setExerciseType(userReplyRepository.getExerciseTypeByUserId(userId));
            reply.setSkipWord(true);
            reply.setBotActivity(lastBotActivity);
            userReplyRepository.save(reply);
        }
        if (showAnswer) {
            UsersReply reply = new UsersReply();
            User user = new User();
            user.setId(userId);
            reply.setUser(user);
            reply.setExerciseType(userReplyRepository.getExerciseTypeByUserId(userId));
            reply.setShowAnswer(true);
            reply.setBotActivity(lastBotActivity);
            userReplyRepository.save(reply);

            Set<String> translations = getTranslation(type, lastBotActivity);
            sendText(chatId, String.join("\n", translations));

            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                log.warn("Caught InterruptedException", e);
            }
        }


        switch (type) {
            case FOREIGN_RUSSIAN -> sendWord(chatId, lastBotActivity.getTopic().getId(), foreignWordRepository::findForeignWordByTopicsId, userId, botActivity::setForeignWord, botActivity);
            case RUSSIAN_FOREIGN -> sendWord(chatId, lastBotActivity.getTopic().getId(), russianWordRepository::findRussianWordByTopicsId, userId, botActivity::setRussianWord, botActivity);
        }
    }

    private void checkThemeAndSendWord(String receivedMessage, long chatId) {
        String prefix = receivedMessage.substring(0, 4);
        Long userId = getUserIdByChatId(chatId, null);
        BotActivity botActivity = new BotActivity();
        switch (prefix) {
            case THEME_PREFIX_FOREIGN -> send(receivedMessage, chatId, THEME_PREFIX_FOREIGN, Topic::getForeignTopic, foreignWordRepository::findForeignWordByTopicsId, userId, botActivity::setForeignWord, botActivity);
            case THEME_PREFIX_RUSSIAN -> send(receivedMessage, chatId, THEME_PREFIX_RUSSIAN, Topic::getTranslation, russianWordRepository::findRussianWordByTopicsId, userId, botActivity::setRussianWord, botActivity);
        }
    }

    private <T extends Word> void send(String receivedMessage, long chatId, String themePrefix,
                                       Function<Topic, String> topicLanguageFunc, Function<Long, List<T>> findWordsByTopicId,
                                       Long userId, Consumer<T> consumer, BotActivity botActivity) {
        Long topicId = topicService.getTopicId(receivedMessage.substring(4), topicLanguageFunc);
        if (topicId != null) {
            sendWord(chatId, topicId, findWordsByTopicId, userId, consumer, botActivity);
        } else {
            sendText(chatId, "Topic is not existing anymore, please choose another");
            sendTextWithAction(chatId, topicService.chooseTheme(topicLanguageFunc, themePrefix), "Please choose topic to learn");
        }
    }

    private <T extends Word> void sendWord(long chatId, Long topicId, Function<Long, List<T>> findWordsByTopicId, Long userId, Consumer<T> consumer, BotActivity botActivity) {
        List<T> wordsByTopic = findWordsByTopicId.apply(topicId);
        if (wordsByTopic.isEmpty()) {
            sendText(chatId, "This topic doesn't contain any words, please choose another.");
        }
        int randomNum = random.nextInt(0, wordsByTopic.size());
        T randomWord = wordsByTopic.get(randomNum);

        // write to DB bot_activity
        Topic topic = new Topic();
        topic.setId(topicId);
        botActivity.setTopic(topic);
        User user = new User();
        user.setId(userId);
        botActivity.setUser(user);
        consumer.accept(randomWord);

        botActivityRepository.save(botActivity);

        sendTextWithAction(chatId, Buttons.exerciseInlineMarkup(), randomWord.getWord());
    }

    private boolean checkTranslation(String receivedMessage, long chatId) {
        //check last sent word from DB
        Long userId = getUserIdByChatId(chatId, null);
        BotActivity lastBotActivity = botActivityRepository.getLastBotActivityByUserId(userId);
        ExerciseType type = lastBotActivity.getForeignWord() != null ? FOREIGN_RUSSIAN : RUSSIAN_FOREIGN; //todo check

        UsersReply reply = new UsersReply();
        User user = new User();
        user.setId(userId);
        reply.setUser(user);
        reply.setExerciseType(lastBotActivity.getForeignWord() != null ? 1 : 2);
        reply.setReply(receivedMessage);
        reply.setBotActivity(lastBotActivity);
        userReplyRepository.save(reply);

        return getTranslation(type, lastBotActivity).contains(receivedMessage.toLowerCase());
    }

    private Set<String> getTranslation(ExerciseType type, BotActivity lastBotActivity) {
        switch (type) {
            case FOREIGN_RUSSIAN -> {
                Long lastSentWordId = lastBotActivity.getForeignWord().getId();
                List<RussianWord> russianWords = russianWordRepository.findRussianWordsByForeignWordsId(lastSentWordId);
                return russianWords.stream().map(w -> w.getWord().toLowerCase()).collect(Collectors.toSet());
            }
            case RUSSIAN_FOREIGN -> {
                Long lastSentWordId = lastBotActivity.getRussianWord().getId();
                List<ForeignWord> russianWords = foreignWordRepository.findForeignWordByRussianWordsId(lastSentWordId);
                return russianWords.stream().map(w -> w.getWord().toLowerCase()).collect(Collectors.toSet());
            }
            default -> { return new HashSet<>(); }
        }
    }

    private void sendTextWithAction(long chatId, InlineKeyboardMarkup keyboardMarkup, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
            log.info("Reply sent: {}", message.getText());
        } catch (TelegramApiException e){
            log.error(e.getMessage());
        }
    }

    private void sendText(long chatId, String textToSend){
        sendTextWithAction(chatId, null, textToSend);
    }

    private Long getUserIdByChatId(Long chatId, String userName) {
        Long userId = userRepository.getUserIdByChatId(chatId);
        if (userId != null) return userId;

        else {
            User user = new User();
            user.setChatId(chatId);
            user.setName(userName);
            return userRepository.save(user).getId();
        }
    }

}
