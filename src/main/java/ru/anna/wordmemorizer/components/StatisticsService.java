package ru.anna.wordmemorizer.components;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.anna.wordmemorizer.entity.ForeignWord;
import ru.anna.wordmemorizer.entity.RussianWord;
import ru.anna.wordmemorizer.entity.Topic;
import ru.anna.wordmemorizer.entity.UsersReply;
import ru.anna.wordmemorizer.repository.ForeignWordRepository;
import ru.anna.wordmemorizer.repository.RussianWordRepository;
import ru.anna.wordmemorizer.repository.TopicRepository;
import ru.anna.wordmemorizer.repository.UserReplyRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.anna.wordmemorizer.WordMemorizerBot.STATISTICS_PREFIX;

@Service
public class StatisticsService {

    private final UserReplyRepository userReplyRepository;

    private final TopicRepository topicRepository;

    private final ForeignWordRepository foreignWordRepository;

    private final RussianWordRepository russianWordRepository;

    public StatisticsService(UserReplyRepository userReplyRepository, TopicRepository topicRepository, ForeignWordRepository foreignWordRepository, RussianWordRepository russianWordRepository) {
        this.userReplyRepository = userReplyRepository;
        this.topicRepository = topicRepository;
        this.foreignWordRepository = foreignWordRepository;
        this.russianWordRepository = russianWordRepository;
    }

    public String getStatistics(Long userId, Long topicId) {
        List<UsersReply> replies = userReplyRepository.getAllByUserIdAndThemeId(userId, topicId);
        if (replies.isEmpty()) return "You haven't tried this theme.";
        int rightAnswers = 0;
        int wrongAnswers = 0;
        int skip = 0;
        int showAnswer = 0;

        for (UsersReply reply: replies) {
            if (reply.getSkipWord() != null && reply.getSkipWord()) {
                skip++;
                continue;
            }
            if (reply.getShowAnswer() != null && reply.getShowAnswer()) {
                showAnswer++;
                continue;
            }
            ForeignWord foreignWord = reply.getBotActivity().getForeignWord();
            if (foreignWord != null) {
                Long lastSentWordId = foreignWord.getId();
                List<RussianWord> russianWords = russianWordRepository.findRussianWordsByForeignWordsId(lastSentWordId);
                Set<String> translations = russianWords.stream().map(w -> w.getWord().toLowerCase()).collect(Collectors.toSet());
                if (translations.contains(reply.getReply().toLowerCase())) {
                    rightAnswers++;
                } else {
                    wrongAnswers++;
                }
                continue;
            }
            RussianWord russianWord = reply.getBotActivity().getRussianWord();
            if (russianWord != null) {
                Long lastSentWordId = russianWord.getId();
                List<ForeignWord> foreignWords = foreignWordRepository.findForeignWordByRussianWordsId(lastSentWordId);
                Set<String> translations = foreignWords.stream().map(w -> w.getWord().toLowerCase()).collect(Collectors.toSet());
                if (translations.contains(reply.getReply().toLowerCase())) {
                    rightAnswers++;
                } else {
                    wrongAnswers++;
                }
            }
        }

        UserStatistics statistics = new UserStatistics(rightAnswers, wrongAnswers, skip, showAnswer);
        return createStatisticsText(statistics);
    }

    public InlineKeyboardMarkup chooseTheme() {

        List<Topic> topics = topicRepository.findAllNonEmptyTopics();
        List<List<InlineKeyboardButton>> rowsInLine = topics.stream()
                .map(topic -> {
                    InlineKeyboardButton button = new InlineKeyboardButton(topic.getForeignTopic() + "-" + topic.getTranslation());
                    button.setCallbackData(STATISTICS_PREFIX + topic.getId());
                    return List.of(button);
                })
                .collect(Collectors.toList());

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(rowsInLine);
        return markupInline;
    }

    private String createStatisticsText(UserStatistics stat) {
        return String.format("""
                        You current results are:
                        number of correct answers: %s,
                        number of wrong answers: %s,
                        number of skipped words: %s,
                        number of showed answers: %s
                        """
                , stat.rightAnswers, stat.wrongAnswers, stat.skip, stat.showAnswer);
    }

    @Data
    @AllArgsConstructor
    public static class UserStatistics {
        private int rightAnswers;
        private int wrongAnswers;
        private int skip;
        private int showAnswer;
    }
}
