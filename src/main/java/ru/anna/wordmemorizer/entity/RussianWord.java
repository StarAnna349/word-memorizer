package ru.anna.wordmemorizer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
@Entity(name = "russian_words")
public class RussianWord implements Word {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "russian_words_sequence_generator")
    @SequenceGenerator(name = "russian_words_sequence_generator", sequenceName = "russian_words_sequence", allocationSize = 1)
    private Long id;
    private String word;

    @ManyToMany(mappedBy = "russianWords")
    Set<ForeignWord> foreignWords = new HashSet<>();

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "russian_topic",
            joinColumns = { @JoinColumn(name = "russian_word_id") },
            inverseJoinColumns = { @JoinColumn(name = "topic_id") }
    )
    Set<Topic> topics = new HashSet<>();
}
