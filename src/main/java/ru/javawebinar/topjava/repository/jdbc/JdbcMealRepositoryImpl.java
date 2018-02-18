package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Repository
public class JdbcMealRepositoryImpl implements MealRepository {

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert simpleJdbcInsert;

    private static final RowMapper<Meal> ROW_MAPPER = new RowMapper<Meal>() {
        @Override
        public Meal mapRow(ResultSet rs, int rowNum) throws SQLException {
            Meal meal = new Meal();
            meal.setId(rs.getInt("id"));
            meal.setDateTime(rs.getTimestamp("datetime").toLocalDateTime());
            meal.setCalories(rs.getInt("calories"));
            meal.setDescription(rs.getString("description"));
            return meal;
        }
    };

    @Autowired
    public JdbcMealRepositoryImpl(DataSource dataSource, JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("meals")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Meal save(Meal meal, int userId) {
        SqlParameterSource sqlParamMap = new MapSqlParameterSource()
                .addValue("id", meal.getId())
                .addValue("user_id", userId)
                .addValue("description", meal.getDescription())
                .addValue("datetime", meal.getDateTime())
                .addValue("calories", meal.getCalories());
        if (meal.isNew()) {
            Number id = simpleJdbcInsert.executeAndReturnKey(sqlParamMap);
            meal.setId(id.intValue());
        } else {
            meal = namedParameterJdbcTemplate.update("UPDATE meals SET description = :description," +
                    " datetime = :datetime, calories = :calories WHERE user_id = :user_id AND id = :id", sqlParamMap) != 0 ? meal : null;
        }
        return meal;
    }

    @Override
    public boolean delete(int id, int userId) {
        return jdbcTemplate.update("DELETE FROM topjava.public.meals WHERE user_id = ? AND id = ?", userId, id) != 0;
    }

    @Override
    public Meal get(int id, int userId) {
         List<Meal> meals = jdbcTemplate.query("SELECT * FROM topjava.public.meals WHERE user_id = ? AND id = ?", ROW_MAPPER, userId, id);
         return DataAccessUtils.singleResult(meals);
    }

    @Override
    public List<Meal> getAll(int userId) {
        return jdbcTemplate.query("SELECT * FROM topjava.public.meals WHERE user_id = ? ORDER BY datetime DESC",ROW_MAPPER, userId);
    }

    @Override
    public List<Meal> getBetween(LocalDateTime startDate, LocalDateTime endDate, int userId) {
        return jdbcTemplate.query("SELECT * FROM (SELECT * FROM topjava.public.meals WHERE datetime BETWEEN  ?::timestamp AND ?::timestamp) as m2 WHERE m2.user_id = ? ORDER BY datetime DESC",
//                ROW_MAPPER, startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")), doesn't work. debug later
//                endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")), userId);
                ROW_MAPPER, startDate.toString(), endDate.toString(), userId);
    }
}
