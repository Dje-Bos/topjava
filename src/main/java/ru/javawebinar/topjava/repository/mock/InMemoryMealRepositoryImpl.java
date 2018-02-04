package ru.javawebinar.topjava.repository.mock;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
@Repository
public class InMemoryMealRepositoryImpl implements MealRepository {
    private Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.MEALS.forEach(meal -> save(meal, 0));
    }

    @Override
    public Meal save(Meal meal, int userID) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
        }
        if (meal.getUserID() != userID) {
            return null;
        }
        repository.put(meal.getId(), meal);
        return meal;
    }

    @Override
    public boolean delete(int id, int userID) {
        if (repository.get(id).getUserID() != userID) {
            return false;
        }
        return repository.remove(id) != null;
    }

    @Override
    public Meal get(int id, int userID) {
        Meal meal = repository.get(id);
        return meal.getUserID() == userID ? meal : null;
    }

    @Override
    public Collection<Meal> getAll(int userID) {
        return repository.values().stream().filter(meal -> meal.getUserID() == userID)
                .sorted(Comparator.comparing(Meal::getDateTime)).collect(Collectors.toList());
    }

    @Override
    public Collection<Meal> getFilteredByDate(int userID, LocalDate start, LocalDate end) {
        return getAll(userID).stream().filter(meal -> DateTimeUtil.isBetweenDate(meal.getDate(), start, end)).collect(Collectors.toList());

    }
}

