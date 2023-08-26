package ru.anna.wordmemorizer.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.anna.wordmemorizer.entity.ForeignWord;
import ru.anna.wordmemorizer.entity.Topic;

import java.util.Collection;
import java.util.List;

@Repository
@Transactional
public interface TopicRepository extends JpaRepository<Topic,Long> {
    @Query(value = "SELECT * FROM topics t WHERE id IN (SELECT topic_id FROM foreign_topic ft)", nativeQuery = true)
    List<Topic> findAllNonEmptyTopics();
}
