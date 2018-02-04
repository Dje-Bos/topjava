package ru.javawebinar.topjava.service;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.List;

public interface MealService {
    Meal save(Meal meal, int userID);
    void delete(int id, int userID);
    Meal get(int id, int userID);
    Meal update(Meal meal, int userID);
    List<Meal> getFilteredByDateTime(int userID, LocalDateTime start, LocalDateTime end);

}