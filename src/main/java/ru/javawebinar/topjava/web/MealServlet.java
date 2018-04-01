package ru.javawebinar.topjava.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.web.meal.MealRestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;

@Controller
@RequestMapping(path = "/meals")
public class MealServlet {

    @Autowired
    private MealRestController mealController;

    @GetMapping
    public String proceessActions(HttpServletRequest request, Model model) {
        String action = request.getParameter("action");
        String modelString = "";
        switch (action == null ? "all" : action) {
            case "delete":
                int id = getId(request);
                mealController.delete(id);
                modelString = "redirect:/meals";
                break;
            case "create":
            case "update":
                final Meal meal = "create".equals(action) ?
                        new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000) :
                        mealController.get(getId(request));
                model.addAttribute("meal", meal);
                modelString = "meal";
                break;
            case "all":
            default:
                model.addAttribute("meals", mealController.getAll());
                modelString = "meals";
                break;
        }
        return modelString;
    }

    @PostMapping(headers = "!id")
    protected String create(@RequestParam LocalDateTime dateTime,
                            @RequestParam String description,
                            @RequestParam Integer calories
    ) {
        Meal meal = new Meal(
                dateTime,
                description,
                calories);
        mealController.create(meal);
        return "redirect:/meals";
    }

    @PostMapping(headers = "id")
    protected String update(@RequestParam LocalDateTime dateTime,
                            @RequestParam String description,
                            @RequestParam Integer calories,
                            @RequestParam Integer id) {
        Meal meal = new Meal(id,
                dateTime,
                description,
                calories);
        mealController.update(meal, id);
        return "redirect:/meals";
    }

    @PostMapping(headers = {"!id", "!calories"})
    protected String filter(Model model, HttpServletRequest request) throws UnsupportedEncodingException {
        request.setCharacterEncoding("UTF-8");
        LocalDate startDate = parseLocalDate(request.getParameter("startDate"));
        LocalDate endDate = parseLocalDate(request.getParameter("endDate"));
        LocalTime startTime = parseLocalTime(request.getParameter("startTime"));
        LocalTime endTime = parseLocalTime(request.getParameter("endTime"));
        model.addAttribute("meals", mealController.getBetween(startDate, startTime, endDate, endTime));
        return "meals";
    }

    private Integer getId(HttpServletRequest request) {
        return Integer.parseInt(Objects.requireNonNull(request.getParameter("id")));
    }


}
