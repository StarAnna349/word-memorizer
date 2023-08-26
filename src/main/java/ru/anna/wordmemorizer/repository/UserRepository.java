package ru.anna.wordmemorizer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.anna.wordmemorizer.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    @Query(value = "SELECT u.id FROM users u WHERE u.chat_id = :chatId ", nativeQuery = true)
    Long getUserIdByChatId(Long chatId);
}
