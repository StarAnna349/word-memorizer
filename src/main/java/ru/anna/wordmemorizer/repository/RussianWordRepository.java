package ru.anna.wordmemorizer.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.anna.wordmemorizer.entity.RussianWord;

import java.util.List;

@Repository
@Transactional
public interface RussianWordRepository extends JpaRepository<RussianWord,Long> {
    List<RussianWord> findRussianWordsByForeignWordsId(Long foreignWordsId);
    List<RussianWord> findRussianWordByTopicsId(Long topicId);
}
