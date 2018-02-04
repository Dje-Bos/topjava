package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.AuthorizedUser;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Controller
public class MealRestController {
    private static final Logger log = LoggerFactory.getLogger(MealRestController.class);
    @Autowired
    private MealService service;

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");

        Meal meal = new Meal(id.isEmpty() ? null : Integer.valueOf(id), AuthorizedUser.id(),
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.valueOf(request.getParameter("calories")));

        log.info(meal.isNew() ? "Create {}" : "Update {}", meal);
        service.save(meal, AuthorizedUser.id());
        response.sendRedirect("meals");
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        switch (action == null ? "all" : action) {
            case "delete":
                int id = getId(request);
                log.info("Delete {}", id);
                service.delete(id, AuthorizedUser.id());
                response.sendRedirect("meals");
                break;
            case "create":
            case "update":
                final Meal meal = "create".equals(action) ?
                        new Meal(AuthorizedUser.id(), LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000) :
                        service.get(getId(request), AuthorizedUser.id());
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/meal.jsp").forward(request, response);
                break;
            case "filter":
                log.info("filter");
                String fromDate = request.getParameter("fromDate");
                String fromTime = request.getParameter("fromTime");
                String toDate = request.getParameter("toDate");
                String toTime = request.getParameter("toTime");
                LocalDate fromLocalDate = fromDate.isEmpty() ? LocalDate.MIN : LocalDate.parse(fromDate);
                LocalTime fromLocalTime = fromTime.isEmpty() ? LocalTime.MIN : LocalTime.parse(fromTime);
                LocalDate toLocalDate = toDate.isEmpty() ? LocalDate.MAX : LocalDate.parse(toDate);
                LocalTime toLocalTime = toTime.isEmpty() ? LocalTime.MAX : LocalTime.parse(toTime);
                LocalDateTime from = LocalDateTime.of(fromLocalDate, fromLocalTime);
                LocalDateTime to = LocalDateTime.of(toLocalDate, toLocalTime);
                request.setAttribute("meals",
                        MealsUtil.getWithExceeded(service.getFilteredByDateTime(AuthorizedUser.id(), from, to), MealsUtil.DEFAULT_CALORIES_PER_DAY));
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
            case "all":
            default:
                log.info("getAll");


                request.setAttribute("meals",
                        MealsUtil.getWithExceeded(service.getFilteredByDateTime(AuthorizedUser.id(), null, null), MealsUtil.DEFAULT_CALORIES_PER_DAY));
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
        }
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.valueOf(paramId);
    }

}
