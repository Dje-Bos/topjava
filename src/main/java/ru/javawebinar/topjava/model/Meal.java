package ru.javawebinar.topjava.model;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
@NamedQueries({
        @NamedQuery(name = "delete", query = "Delete From Meal m Where m.user.id = :userId and m.id = :id"),
        @NamedQuery(name = "getAll", query = "SELECT m FROM Meal m WHERE m.user.id = :userId order by m.dateTime desc "),
        @NamedQuery(name = "getBetween", query = "SELECT m FROM Meal m WHERE m.user.id = :userId and m.dateTime between :start and :end order by m.dateTime desc"),
        @NamedQuery(name = "get", query = "select m from Meal m WHERE m.user.id = :userId and m.id = :id"),
        @NamedQuery(name = "update", query = "update Meal set calories = :calories, dateTime = :datetime, description = :description WHERE user.id = :userId and id = :id")
}
)
@Entity
@Table(name = "meals", indexes = @Index(name = "meals_unique_user_datetime_idx", unique = true, columnList = "user_id, date_time"))
public class Meal extends BaseEntity {

    @Column(name = "date_time", nullable = false)
    @NotNull
    private LocalDateTime dateTime;

    @Column(name = "description")
    @NotBlank
    private String description;

    @Column(name = "calories", nullable = false, columnDefinition = "integer default 2000")
    @Range(min = 0, max = 10000)
    private int calories;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    public Meal() {
    }

    public Meal(LocalDateTime dateTime, String description, int calories) {
        this(null, dateTime, description, calories);
    }

    public Meal(Integer id, LocalDateTime dateTime, String description, int calories) {
        super(id);
        this.dateTime = dateTime;
        this.description = description;
        this.calories = calories;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getDescription() {
        return description;
    }

    public int getCalories() {
        return calories;
    }

    public LocalDate getDate() {
        return dateTime.toLocalDate();
    }

    public LocalTime getTime() {
        return dateTime.toLocalTime();
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Meal{" +
                "id=" + id +
                ", dateTime=" + dateTime +
                ", description='" + description + '\'' +
                ", calories=" + calories +
                '}';
    }
}
