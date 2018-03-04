package ru.javawebinar.topjava.repository.jpa;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class JpaMealRepositoryImpl implements MealRepository {

    @PersistenceContext
    EntityManager em;

    @Transactional
    @Override
    public Meal save(Meal meal, int userId) {
        if (meal.isNew()) {
            em.persist(meal);
            return meal;
        }
        else {
             return em.createNamedQuery("update")
                    .setParameter("datetime", meal.getDateTime())
                    .setParameter("description", meal.getDescription())
                    .setParameter("calories", meal.getCalories())
                     .setParameter("id", meal.getId())
                    .setParameter("userId", userId).executeUpdate() == 0 ? null : em.find(Meal.class, meal.getId());
        }
    }

    @Transactional
    @Override
    public boolean delete(int id, int userId) {
        return em.createNamedQuery("delete").
                setParameter("userId", userId).setParameter("id", id).executeUpdate() != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        List<Meal> mealList = em.createNamedQuery("get", Meal.class).setParameter("id", id)
                .setParameter("userId", userId).getResultList();
        return mealList.isEmpty() ? null : DataAccessUtils.singleResult(mealList);
    }

    @Override
    public List<Meal> getAll(int userId) {

        return em.createNamedQuery("getAll", Meal.class).setParameter("userId", userId).getResultList();
    }

    @Override
    public List<Meal> getBetween(LocalDateTime startDate, LocalDateTime endDate, int userId) {
        return em.createNamedQuery("getBetween", Meal.class).
                setParameter("userId", userId)
                .setParameter("start", startDate)
                .setParameter("end", endDate)
                .getResultList();
    }
}