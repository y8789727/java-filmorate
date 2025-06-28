package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaRatingController {
    @Autowired
    private FilmService filmService;

    @GetMapping
    public Collection<MpaRating> getAll() {
        return filmService.getAllMpaRatings();
    }

    @GetMapping("/{id}")
    public MpaRating getById(@PathVariable int id) {
        return filmService.getMpaRatingById(id);
    }

}
