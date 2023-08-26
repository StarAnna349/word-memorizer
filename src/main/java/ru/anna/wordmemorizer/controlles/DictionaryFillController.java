package ru.anna.wordmemorizer.controlles;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.anna.wordmemorizer.dto.Dictionary;
import ru.anna.wordmemorizer.entity.ForeignWord;
import ru.anna.wordmemorizer.entity.RussianWord;
import ru.anna.wordmemorizer.entity.Topic;
import ru.anna.wordmemorizer.repository.ForeignWordRepository;
import ru.anna.wordmemorizer.repository.RussianWordRepository;
import ru.anna.wordmemorizer.repository.TopicRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping(value = "/dictionary")
public class DictionaryFillController {

    private final TopicRepository topicRepository;
    private final ForeignWordRepository foreignWordRepository;
    private final RussianWordRepository russianWordRepository;

    public DictionaryFillController(TopicRepository topicRepository, ForeignWordRepository foreignWordRepository, RussianWordRepository russianWordRepository) {
        this.topicRepository = topicRepository;
        this.foreignWordRepository = foreignWordRepository;
        this.russianWordRepository = russianWordRepository;
    }

    @PostMapping(value = "/{topic}")
    @Transactional
    @ResponseBody
    public String fillDictionary(@RequestBody Dictionary dictionary, @PathVariable String topic) {
        String[] top = topic.split("-");
        Topic topicEntity = new Topic();
        topicEntity.setForeignTopic(top[0]);
        topicEntity.setTranslation(top[1]);
        List<ForeignWord> foreignWords = new ArrayList<>();
        List<RussianWord> russianWords = new ArrayList<>();
        for (Dictionary.Element e: dictionary.getElements()){
            ForeignWord foreignWord = new ForeignWord();
            RussianWord russianWord = new RussianWord();

            foreignWord.setWord(e.getEng());
            russianWord.setWord(e.getRus());
            Set<Topic> topics = new HashSet<>();
            topics.add(topicEntity);
            Set<RussianWord> russianWordSet = new HashSet<>();
            russianWordSet.add(russianWord);
            Set<ForeignWord> foreignWordSet = new HashSet<>();
            foreignWordSet.add(foreignWord);

            foreignWord.setRussianWords(russianWordSet);
            foreignWord.setTopics(topics);

            russianWord.setTopics(topics);
            russianWord.setForeignWords(foreignWordSet);

            foreignWords.add(foreignWord);
            russianWords.add(russianWord);
        }
        topicRepository.save(topicEntity);
        foreignWordRepository.saveAll(foreignWords);
        russianWordRepository.saveAll(russianWords);
        return "Ok";
    }

}
