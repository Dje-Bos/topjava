package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.javawebinar.topjava.AuthorizedUser;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

public class UserServlet extends HttpServlet {
    private static final Logger log = getLogger(UserServlet.class);
    private static final ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("/spring/spring-app.xml");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("forward to users");
        request.getRequestDispatcher("/users.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String authUser = req.getParameter("authorizedUser");
        Integer userID = authUser.isEmpty() || authUser == null ? 0 : Integer.valueOf(authUser);
        AuthorizedUser.setID(userID);
        resp.sendRedirect("meals");
    }

    @Override
    public void destroy() {
        super.destroy();
        context.close();
    }
}
