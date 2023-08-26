package ru.anna.wordmemorizer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@RequiredArgsConstructor
@Entity(name = "foreign_words")
public class ForeignWord implements Word {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "foreign_words_sequence_generator")
    @SequenceGenerator(name = "foreign_words_sequence_generator", sequenceName = "foreign_words_sequence", allocationSize = 1)
    private Long id;
    private String word;

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "foreign_russian",
            joinColumns = { @JoinColumn(name = "foreign_id") },
            inverseJoinColumns = { @JoinColumn(name = "russian_id") }
    )
    Set<RussianWord> russianWords = new HashSet<>();

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "foreign_topic",
            joinColumns = { @JoinColumn(name = "foreign_word_id") },
            inverseJoinColumns = { @JoinColumn(name = "topic_id") }
    )
    Set<Topic> topics = new HashSet<>();
}
