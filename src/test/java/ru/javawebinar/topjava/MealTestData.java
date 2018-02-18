package ru.javawebinar.topjava;

import ru.javawebinar.topjava.matcher.BeanMatcher;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class MealTestData {

    public static final BeanMatcher<Meal> MATCHER = new BeanMatcher<>((expected, actual) -> Objects.equals(expected.getCalories(), actual.getCalories()) &&
                            Objects.equals(expected.getDateTime(), actual.getDateTime()) &&
                            Objects.equals(expected.getDescription(), actual.getDescription()) &&
                            Objects.equals(expected.getId(), actual.getId()));
public static final Meal ADMIN_MEAL_1 =  new Meal (100002, LocalDateTime.parse("2018-06-12T06:45"),  "breakfast", 200);
public static final Meal ADMIN_MEAL_2 =  new Meal (100003, LocalDateTime.parse("2018-06-12T11:38"),  "lunch", 320);
public static final Meal ADMIN_MEAL_3 =  new Meal (100004, LocalDateTime.parse("2018-06-12T18:11"),  "supper", 450);
public static final Meal ADMIN_MEAL_4 =  new Meal (100005, LocalDateTime.parse("2018-06-13T10:59"),  "lunch", 780);
public static final Meal USER_MEAL_1 =  new Meal (100006, LocalDateTime.parse("2018-06-12T12:58"),  "supper", 540);
public static final Meal USER_MEAL_2 =  new Meal (100007, LocalDateTime.parse("2018-06-12T12:59"),  "dinner", 1200);
}
