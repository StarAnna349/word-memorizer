package ru.anna.wordmemorizer.components;

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.List;

public interface BotCommands {
    String START_COMMAND = "/start";
    String HELP_COMMAND = "/help";
    String CHOOSE_ENG_THEME_COMMAND = "/choose_eng_theme";
    String CHOOSE_RUS_THEME_COMMAND = "/choose_rus_theme";
    String SHOW_STATISTICS_COMMAND = "/show_stat";
    String SKIP_COMMAND = "/skip";
    String SHOW_ANSWER_COMMAND = "/answer";

    List<BotCommand> LIST_OF_COMMANDS = List.of(
            new BotCommand(START_COMMAND, "start bot"),
            new BotCommand(HELP_COMMAND, "bot info"),
            new BotCommand(CHOOSE_ENG_THEME_COMMAND, "begin memorizing new words from foreign to russian language"),
            new BotCommand(CHOOSE_RUS_THEME_COMMAND, "begin memorizing new words from russian to foreign language"),
            new BotCommand(SHOW_STATISTICS_COMMAND, "get exercise statistics")
    );

    String HELP_TEXT = String.format("""
            This bot will help you to lean the translation from foreign to russian language and vice versa.
            The following commands are available to you:
            %s - start the bot
            %s - help menu
            %s- begin memorizing new words foreign -> russian language
            %s - begin memorizing new words russian -> foreign language
            %s - get exercise statistics""",
            START_COMMAND, HELP_COMMAND, CHOOSE_ENG_THEME_COMMAND, CHOOSE_RUS_THEME_COMMAND, SHOW_STATISTICS_COMMAND);
}
