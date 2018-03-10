package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.List;

public interface CrudMealRepository extends JpaRepository<Meal, Integer> {


    List<Meal> findAllByUserIdOrderByDateTimeDesc(int userId);

    @Override
    @Query("select m FROM Meal m LEFT JOIN FETCH m.user WHERE m.id = :id")
    Meal getOne(@Param("id") Integer integer);

    @Transactional
    @Modifying
    int deleteByUserIdAndId(int userId, int id);

    List<Meal> getAllByUserIdAndDateTimeBetweenOrderByDateTimeDesc(int userId, LocalDateTime start, LocalDateTime end);
}
