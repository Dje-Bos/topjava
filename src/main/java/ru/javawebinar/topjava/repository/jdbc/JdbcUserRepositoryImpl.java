package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.AbstractNamedEntity;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import javax.sql.DataSource;
import java.util.*;

@Transactional(readOnly = true)
@Repository
public class JdbcUserRepositoryImpl implements UserRepository {

    private static final ResultSetExtractor<List<User>> ROW_EXTRACTOR = rs -> {
        Set<User> users = new HashSet<>();
        while (rs.next()) {
            User user = containsWithId(rs.getInt("id"), users);
            if (user.getId() == null) {
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setCaloriesPerDay(rs.getInt("calories_per_day"));
                user.setEnabled(rs.getBoolean("enabled"));
                user.setPassword(rs.getString("password"));
                user.setRoles(EnumSet.of(Role.valueOf(rs.getString("role"))));
            } else {
                user.getRoles().add(Role.valueOf(rs.getString("role")));
            }
            users.add(user);
        }

        return new ArrayList<>(users);
    };

    private static final BeanPropertyRowMapper<User> ROW_MAPPER = BeanPropertyRowMapper.newInstance(User.class);


    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    @Autowired
    public JdbcUserRepositoryImpl(DataSource dataSource, JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(dataSource)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    private static User containsWithId(int id, Set<User> set) {
        return set.stream().filter(user -> user.getId() == id).findFirst().orElse(new User());
    }

    @Transactional
    @Override
    public User save(User user) {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);
        Role[] roleArr = user.getRoles().toArray(new Role[0]);
        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
            String query = "INSERT INTO user_roles (user_id, role) VALUES (:user_id, :role)";
            SqlParameterSource[] sqlParameterSource = Arrays.stream(roleArr).map(role -> new MapSqlParameterSource()
                    .addValue("user_id", user.getId())
                    .addValue("role", role.toString())).toArray(MapSqlParameterSource[]::new);
            namedParameterJdbcTemplate.batchUpdate(query, sqlParameterSource);
        } else {
            SqlParameterSource[] sqlParameterUpdateSource = Arrays.stream(roleArr).map(role -> new MapSqlParameterSource()
                    .addValue("user_id", user.getId())
                    .addValue("role", role.toString())).toArray(MapSqlParameterSource[]::new);
            namedParameterJdbcTemplate.batchUpdate("UPDATE user_roles SET role=:role WHERE user_id=:user_id",
                    sqlParameterUpdateSource);
            namedParameterJdbcTemplate.update(
                    "UPDATE users SET name=:name, email=:email, password=:password, " +
                            "registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id", parameterSource);
        }
        return user;
    }

    @Transactional
    @Override
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users LEFT JOIN user_roles ON users.id = user_roles.user_id WHERE id=" + id, ROW_EXTRACTOR);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public User getByEmail(String email) {
        //return jdbcTemplate.queryForObject("SELECT * FROM users WHERE email=?", ROW_MAPPER, email);
        List<User> users = jdbcTemplate.query("SELECT * FROM users LEFT JOIN user_roles ON users.id = user_roles.user_id WHERE users.email=?", ps -> ps.setString(1, email), ROW_EXTRACTOR);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public List<User> getAll() {
        List<User> users = jdbcTemplate.query("SELECT * FROM users LEFT JOIN user_roles ON users.id = user_roles.user_id", ROW_EXTRACTOR);
        Comparator<User> comparatorByName = Comparator.comparing(AbstractNamedEntity::getName);
        Comparator<User> comparatorByEmail = Comparator.comparing(User::getEmail);
        users.sort(comparatorByName.thenComparing(comparatorByEmail));
        return users;
    }
}
