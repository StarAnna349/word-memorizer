package ru.anna.wordmemorizer.dto;

import lombok.Data;

import java.util.List;

@Data
public class Dictionary {
    private List<Element> elements;

    @Data
    public static class Element {
        private String eng;
        private String rus;
    }
}
