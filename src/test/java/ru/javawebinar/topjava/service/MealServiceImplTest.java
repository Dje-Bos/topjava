package ru.javawebinar.topjava.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.DbPopulator;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.Arrays;

import static ru.javawebinar.topjava.MealTestData.*;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
public class MealServiceImplTest {
    @Autowired
    DbPopulator dbPopulator;

    @Autowired
    private MealService service;

    @Before
    public void setUp() {
        dbPopulator.execute();
    }

    @Test
    public void get() {
        MATCHER.assertEquals(ADMIN_MEAL_1, service.get(ADMIN_MEAL_1.getId(), UserTestData.ADMIN_ID));
    }

    @Test(expected = NotFoundException.class)
    public void getForeignMeal() {
        service.get(ADMIN_MEAL_1.getId(), UserTestData.USER_ID);
    }

    @Test
    public void delete() {
        service.delete(ADMIN_MEAL_1.getId(), UserTestData.ADMIN_ID);
        MATCHER.assertCollectionEquals(Arrays.asList(ADMIN_MEAL_4, ADMIN_MEAL_3, ADMIN_MEAL_2 ), service.getAll(UserTestData.ADMIN_ID));
    }

    @Test(expected = NotFoundException.class)
    public void deleteForeignMeal() {
        service.delete(ADMIN_MEAL_1.getId(), UserTestData.USER_ID);
    }

    @Test
    public void getBetweenDateTimes() {
        MATCHER.assertCollectionEquals(Arrays.asList(ADMIN_MEAL_3, ADMIN_MEAL_2, ADMIN_MEAL_1), service.getBetweenDateTimes(LocalDateTime.parse("2018-06-12T00:45:00"), LocalDateTime.parse("2018-06-13T00:45:00"), UserTestData.ADMIN_ID));
    }

    @Test
    public void getAll() {
        MATCHER.assertCollectionEquals(Arrays.asList(ADMIN_MEAL_4, ADMIN_MEAL_3, ADMIN_MEAL_2, ADMIN_MEAL_1), service.getAll(UserTestData.ADMIN_ID));
        MATCHER.assertCollectionEquals(Arrays.asList(USER_MEAL_2, USER_MEAL_1), service.getAll(UserTestData.USER_ID));
    }

    @Test
    public void update() {
        MATCHER.assertEquals(new Meal(USER_MEAL_1.getId(), USER_MEAL_1.getDateTime(), "supper", 340), service.update(new Meal(USER_MEAL_1.getId(), USER_MEAL_1.getDateTime(), "supper", 340), UserTestData.USER_ID));
    }

    @Test(expected = NotFoundException.class)
    public void updateForeignFood() {
        service.update(new Meal(USER_MEAL_1.getId(), USER_MEAL_1.getDateTime(), "supper", 340), UserTestData.ADMIN_ID);
    }

    @Test
    public void save() {
        Meal savedMeal = service.save(new Meal(LocalDateTime.now(), "supper", 340), UserTestData.USER_ID);
        MATCHER.assertCollectionEquals(Arrays.asList(USER_MEAL_2, USER_MEAL_1, savedMeal), service.getAll(UserTestData.USER_ID));
    }
}