package ru.anna.wordmemorizer.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@Entity(name = "users_reply")
public class UsersReply {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_reply_sequence_generator")
    @SequenceGenerator(name = "users_reply_sequence_generator", sequenceName = "users_reply_sequence", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Integer exerciseType;

    private String reply;

    private Boolean skipWord;

    private Boolean showAnswer;

    @ManyToOne
    @JoinColumn(name = "bot_activity_id")
    private BotActivity botActivity;

}
