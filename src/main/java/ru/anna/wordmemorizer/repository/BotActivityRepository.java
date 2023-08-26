package ru.anna.wordmemorizer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.anna.wordmemorizer.entity.BotActivity;

@Repository
public interface BotActivityRepository extends JpaRepository<BotActivity,Long> {
    @Query(value = "SELECT * FROM bot_activity WHERE user_id = :userId AND created_at IS NOT NULL\n" +
            "ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
    BotActivity getLastBotActivityByUserId(Long userId);
}
