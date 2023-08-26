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
@Entity(name = "topics")
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "topics_sequence_generator")
    @SequenceGenerator(name = "topics_sequence_generator", sequenceName = "topics_sequence", allocationSize = 1)
    private Long id;
    private String foreignTopic;
    private String translation;

    @ManyToMany(mappedBy = "topics")
    Set<ForeignWord> foreignWords = new HashSet<>();
}
