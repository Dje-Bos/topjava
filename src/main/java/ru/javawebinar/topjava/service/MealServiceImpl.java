package ru.javawebinar.topjava.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class MealServiceImpl implements MealService {
    @Autowired
    private MealRepository repository;

    @Override
    public Meal save(Meal meal, int userID) {
        Meal userMeal = repository.save(meal, userID);
        if (userMeal==null) {
            throw new NotFoundException("");
        }
        else
        return userMeal;
    }

    @Override
    public void delete(int id, int userID) {
        if (!repository.delete(id, userID)) {
            throw new NotFoundException("Meal " + id + " isn't exist or pertain to userID " + userID);
        }

    }

    @Override
    public Meal get(int id, int userID) {
        Meal meal = repository.get(id, userID);
        if (meal == null) {
            throw new NotFoundException("Meal " + id + " isn't exist or pertain to userID " + userID);
        }
        else
        return meal;
    }

    @Override
    public Meal update(Meal meal, int userID) {
        Meal userMeal = repository.save(meal, userID);
        if (userMeal == null) {
            throw new NotFoundException("Meal " + meal.getId() + " isn't exist or not pertain to userID " + userID);
        }
        else
        return userMeal;
    }

    @Override
    public List<Meal> getFilteredByDateTime(int userID, LocalDateTime start, LocalDateTime end) {
        start = start == null ? LocalDateTime.MIN : start;
        end = end == null ? LocalDateTime.MAX : end;
        Collection<Meal> mealCollection = repository.getFilteredByDate(userID, start.toLocalDate(), end.toLocalDate());

        return new ArrayList<>(mealCollection);
    }
}