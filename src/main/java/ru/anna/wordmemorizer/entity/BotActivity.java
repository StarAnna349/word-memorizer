package ru.anna.wordmemorizer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@RequiredArgsConstructor
@Entity(name = "bot_activity")
public class BotActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bot_activity_sequence_generator")
    @SequenceGenerator(name = "bot_activity_sequence_generator", sequenceName = "bot_activity_sequence", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @ManyToOne
    @JoinColumn(name = "foreign_word_id")
    private ForeignWord foreignWord;

    @ManyToOne
    @JoinColumn(name = "russian_word_id")
    private RussianWord russianWord;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;
}
