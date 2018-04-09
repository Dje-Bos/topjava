package ru.javawebinar.topjava.web.meal;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.web.AbstractControllerTest;
import ru.javawebinar.topjava.web.json.JsonUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.javawebinar.topjava.MealTestData.*;

public class MealRestControllerTest extends AbstractControllerTest {
    private static final String REST_URL = MealRestController.REST_URL + '/';

    @Autowired
    MealService mealService;

    @Test
    public void testGet() throws Exception {
        mockMvc.perform(get(REST_URL + MEAL1.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(JsonUtil.writeValue(MEAL1)));
    }

    @Test
    public void testUpdate() throws Exception {
        Meal updatedMeal = new Meal(MEAL1);
        updatedMeal.setDescription("Supper");
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + MEAL1_ID).contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedMeal)))
                .andDo(print())
                .andExpect(status().isOk());
        assertThat(mealService.get(MEAL1_ID, UserTestData.USER_ID)).isEqualToIgnoringGivenFields(updatedMeal, "user");
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + MEAL4.getId()))
                .andExpect(status().isOk())
                .andDo(print());
        ArrayList<Meal> expected = new ArrayList<>(MEALS);
        expected.remove(MEAL4);
        assertThat(mealService.getAll(UserTestData.USER_ID)).isEqualTo(expected);
    }

    @Test
    public void testFilter() throws Exception {
        LinkedMultiValueMap<String, String> valueMap = new LinkedMultiValueMap<>();
        valueMap.add("startDate", LocalDate.of(2015, 4, 1).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        valueMap.add("startTime", LocalTime.of(4, 0).format(DateTimeFormatter.ofPattern("HH:mm")));//LocalTime.of(12, 0).format(DateTimeFormatter.ofPattern("hh:mm")
        valueMap.add("endDate", LocalDate.of(2018, 4, 9).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        valueMap.add("endTime", LocalTime.of(23, 19).format(DateTimeFormatter.ofPattern("HH:mm")));//

        mockMvc.perform(get(REST_URL + "filter?").params(valueMap))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonContent(MEALS));
    }

    @Test
    public void testCreate() throws Exception {
        Meal meal = new Meal(LocalDateTime.now(), "Supper", 390);
        System.out.println(JsonUtil.writeValue(meal));
        mockMvc.perform(MockMvcRequestBuilders.post(REST_URL).content(JsonUtil.writeValue(meal)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(content().json(JsonUtil.writeValue(meal)))
                .andExpect(header().string("location", "http://localhost/rest/meals/100010"));
    }

    @Test
    public void getAll() throws Exception {
        mockMvc.perform(get(REST_URL)).andExpect(content().json(JsonUtil.writeValue(MEALS))).andExpect(status().isOk()).andDo(print());
    }
}