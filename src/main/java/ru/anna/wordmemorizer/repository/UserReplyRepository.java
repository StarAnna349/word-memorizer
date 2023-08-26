package ru.anna.wordmemorizer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.anna.wordmemorizer.entity.UsersReply;

import java.util.List;

@Repository
public interface UserReplyRepository extends JpaRepository<UsersReply,Long> {
    @Query(value = "SELECT ur.exercise_type FROM users_reply ur WHERE ur.exercise_type IS NOT NULL AND ur.user_id = :userId AND ur.created_at IS NOT NULL\n" +
            "ORDER BY ur.created_at DESC LIMIT 1", nativeQuery = true)
    Integer getExerciseTypeByUserId(Long userId);

    @Query(value = "SELECT * FROM users_reply ur WHERE ur.user_id = :userId " +
            "AND ur.bot_activity_id IN (SELECT ba.id FROM bot_activity ba WHERE ba.user_id = :userId AND ba.topic_id = :topicId)", nativeQuery = true)
    List<UsersReply> getAllByUserIdAndThemeId(Long userId, Long topicId);
}
