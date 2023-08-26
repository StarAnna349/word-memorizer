package ru.anna.wordmemorizer.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.anna.wordmemorizer.entity.ForeignWord;

import java.util.List;

@Repository
@Transactional
public interface ForeignWordRepository extends JpaRepository<ForeignWord,Long> {
    List<ForeignWord> findForeignWordByRussianWordsId(Long russianWordsId);
    List<ForeignWord> findForeignWordByTopicsId(Long topicId);
}
