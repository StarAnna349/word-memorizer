package ru.anna.wordmemorizer.components;

public enum ExerciseType {
    FOREIGN_RUSSIAN(1),
    RUSSIAN_FOREIGN(2);

    private int id;

    ExerciseType(int id) {
        this.id = id;
    }

    public static ExerciseType getExerciseTypeById(int id) {
        return switch (id) {
            case 1 -> FOREIGN_RUSSIAN;
            case 2 -> RUSSIAN_FOREIGN;
            default -> throw new RuntimeException("");
        };
    }
}
