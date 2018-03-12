package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.Meal;

import java.util.List;


@ActiveProfiles(Profiles.DATAJPA)
public class DataJpaMealServiceTest extends MealServiceTest {

    @Test
    public void testGetAllWithUser() {
        List<Meal> mealList = service.getAllWithUser(100000);
        MealTestData.MATCHER.assertCollectionEquals(MealTestData.MEALS, mealList);
        UserTestData.MATCHER.assertEquals(UserTestData.USER, mealList.get(0).getUser());
    }
}
