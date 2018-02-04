package ru.javawebinar.topjava;

import static ru.javawebinar.topjava.util.MealsUtil.DEFAULT_CALORIES_PER_DAY;

public class AuthorizedUser {
    private static int ID = 0;
    public static int id() {
        return ID;
    }

    public static void setID(int ID) {
        AuthorizedUser.ID = ID;
    }

    public static int getCaloriesPerDay() {
        return DEFAULT_CALORIES_PER_DAY;
    }
}