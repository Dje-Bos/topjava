package ru.javawebinar.topjava.service;

import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(MealServiceTest.class);

    private static final Map<String, DurationTuple> durationMap = new HashMap<>();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Rule
    public TestWatcher testWatcher = new TestWatcher() {

        @Override
        protected void succeeded(Description description) {
            super.succeeded(description);
        }

        @Override
        protected void failed(Throwable e, Description description) {
            super.failed(e, description);
        }

        @Override
        protected void skipped(AssumptionViolatedException e, Description description) {
            super.skipped(e, description);
        }

        @Override
        protected void starting(Description description) {
            DurationTuple durationTuple = new DurationTuple();
            durationTuple.setStart(Duration.ofMillis(System.currentTimeMillis()));
            durationMap.put(description.getMethodName(), durationTuple);
        }

        @Override
        protected void finished(Description description) {
            DurationTuple existingTupleFromMap = durationMap.get(description.getMethodName());
            existingTupleFromMap.setEnd(Duration.ofMillis(System.currentTimeMillis()));
        }
    };
    static {
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    @Test
    public void testDelete() throws Exception {
        service.delete(MEAL1_ID, USER_ID);
        MATCHER.assertCollectionEquals(Arrays.asList(MEAL6, MEAL5, MEAL4, MEAL3, MEAL2), service.getAll(USER_ID));
    }

    @Test()
    public void testDeleteNotFound() throws Exception {
        expectedException.expect(NotFoundException.class);
        service.delete(MEAL1_ID, 1);
    }

    @Test
    public void testSave() throws Exception {
        Meal created = getCreated();
        service.create(created, USER_ID);
        MATCHER.assertCollectionEquals(Arrays.asList(created, MEAL6, MEAL5, MEAL4, MEAL3, MEAL2, MEAL1), service.getAll(USER_ID));
    }

    @Test
    public void testGet() throws Exception {
        Meal actual = service.get(ADMIN_MEAL_ID, ADMIN_ID);
        MATCHER.assertEquals(ADMIN_MEAL1, actual);
    }

    @Test()
    public void testGetNotFound() throws Exception {
        expectedException.expect(NotFoundException.class);
        service.get(MEAL1_ID, ADMIN_ID);
    }

    @Test
    public void testUpdate() throws Exception {
        Meal updated = getUpdated();
        service.update(updated, USER_ID);
        MATCHER.assertEquals(updated, service.get(MEAL1_ID, USER_ID));
    }

    @Test()
    public void testUpdateNotFound() throws Exception {
        expectedException.expect(NotFoundException.class);
        service.update(MEAL1, ADMIN_ID);
    }

    @Test
    public void testGetAll() throws Exception {
        MATCHER.assertCollectionEquals(MEALS, service.getAll(USER_ID));
    }

    @Test
    public void testGetBetween() throws Exception {
        MATCHER.assertCollectionEquals(Arrays.asList(MEAL3, MEAL2, MEAL1),
                service.getBetweenDates(
                        LocalDate.of(2015, Month.MAY, 30),
                        LocalDate.of(2015, Month.MAY, 30), USER_ID));
    }
    static class DurationTuple {
        private Duration start;
        private Duration end;
        private Duration between;

        public DurationTuple(Duration start, Duration end) {
            this.start = start;
            this.end = end;
            calculateBetween();
        }

        public DurationTuple() {
        }

        public void setStart(Duration start) {
            this.start = start;
            if (end != null)
                calculateBetween();
            else return;
        }

        public void setEnd(Duration end) {
            this.end = end;
            if (start != null)
                calculateBetween();
            else return;
        }

        public Duration getBetween() {
            return between;
        }
        private void calculateBetween() {
            between = end.minus(start);
        }

    }
    @AfterClass
    public static void afterAll() {
        durationMap.forEach((key, value) -> LOGGER.info("Execution time for test \"{}\" is: {}", key, value.getBetween().toMillis()));
    }
}