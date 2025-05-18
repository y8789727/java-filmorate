package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * Film.
 */
@Data
public class Film {
    int id;
    @NotNull
    String name;
    String description;
    LocalDate releaseDate;
    int duration;
}
