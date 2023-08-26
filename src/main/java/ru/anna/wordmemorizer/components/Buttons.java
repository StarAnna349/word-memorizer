package ru.anna.wordmemorizer.components;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

import static ru.anna.wordmemorizer.components.BotCommands.*;

public class Buttons {
    private static final InlineKeyboardButton START_BUTTON = new InlineKeyboardButton("Start");
    private static final InlineKeyboardButton HELP_BUTTON = new InlineKeyboardButton("Help");
    private static final InlineKeyboardButton STUDY_FROM_FOREIGN_BUTTON = new InlineKeyboardButton("Study English -> Russian");
    private static final InlineKeyboardButton STUDY_FROM_RUSSIAN_BUTTON = new InlineKeyboardButton("Study Russian -> English");
    private static final InlineKeyboardButton STATISTICS_BUTTON = new InlineKeyboardButton("Show statistics");

    private static final InlineKeyboardButton SKIP_BUTTON = new InlineKeyboardButton("Skip this word");
    private static final InlineKeyboardButton SHOW_ANSWER_BUTTON = new InlineKeyboardButton("Show answer");

    public static InlineKeyboardMarkup inlineMarkup() {
        START_BUTTON.setCallbackData(START_COMMAND);
        HELP_BUTTON.setCallbackData(HELP_COMMAND);
        STUDY_FROM_FOREIGN_BUTTON.setCallbackData(CHOOSE_ENG_THEME_COMMAND);
        STUDY_FROM_RUSSIAN_BUTTON.setCallbackData(CHOOSE_RUS_THEME_COMMAND);
        STATISTICS_BUTTON.setCallbackData(SHOW_STATISTICS_COMMAND);

        List<List<InlineKeyboardButton>> rowsInLine = List.of(
                List.of(START_BUTTON),
                List.of(HELP_BUTTON),
                List.of(STUDY_FROM_FOREIGN_BUTTON),
                List.of(STUDY_FROM_RUSSIAN_BUTTON),
                List.of(STATISTICS_BUTTON));

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(rowsInLine);

        return markupInline;
    }

    public static InlineKeyboardMarkup skipWord() {
        return createInlineKeyboardMarkup(SKIP_BUTTON, SKIP_COMMAND);
    }

    public static InlineKeyboardMarkup showAnswer() {
        return createInlineKeyboardMarkup(SHOW_ANSWER_BUTTON, SHOW_ANSWER_COMMAND);
    }

    public static InlineKeyboardMarkup exerciseInlineMarkup() {
        SKIP_BUTTON.setCallbackData(SKIP_COMMAND);
        SHOW_ANSWER_BUTTON.setCallbackData(SHOW_ANSWER_COMMAND);

        List<List<InlineKeyboardButton>> rowsInLine = List.of(
                List.of(SKIP_BUTTON),
                List.of(SHOW_ANSWER_BUTTON));

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(rowsInLine);

        return markupInline;
    }

    private static InlineKeyboardMarkup createInlineKeyboardMarkup(InlineKeyboardButton button, String command) {
        button.setCallbackData(command);

        List<InlineKeyboardButton> rowInline = List.of(button);
        List<List<InlineKeyboardButton>> rowsInLine = List.of(rowInline);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(rowsInLine);

        return markupInline;
    }

}
