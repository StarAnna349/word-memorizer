package ru.anna.wordmemorizer.components;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.anna.wordmemorizer.entity.Topic;
import ru.anna.wordmemorizer.repository.TopicRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class TopicService {
    private final TopicRepository topicRepository;

    public TopicService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public InlineKeyboardMarkup chooseTheme(Function<Topic, String> topicLanguageFunc, String prefix) {

        List<Topic> topics = topicRepository.findAllNonEmptyTopics();
        List<List<InlineKeyboardButton>> rowsInLine = topics.stream()
                .map(topic -> {
                    InlineKeyboardButton button = new InlineKeyboardButton(topicLanguageFunc.apply(topic));
                    button.setCallbackData(prefix + topicLanguageFunc.apply(topic));
                    return List.of(button);
                })
                .collect(Collectors.toList());

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(rowsInLine);
        return markupInline;
    }

    public Long getTopicId(String message, Function<Topic, String> topicLanguageFunc) {
        return topicRepository.findAll().stream().filter(t -> message.equals(topicLanguageFunc.apply(t))).findFirst().orElse(new Topic()).getId();
    }
}
