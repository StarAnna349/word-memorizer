package ru.anna.wordmemorizer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@Entity(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_sequence_generator")
    @SequenceGenerator(name = "users_sequence_generator", sequenceName = "users_sequence", allocationSize = 1)
    private Long id;
    private Long chatId;
    private String name;

    @OneToMany(mappedBy = "user")
    private List<UsersReply> replies;
}
