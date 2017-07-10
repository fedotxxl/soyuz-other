package io.belov.soyuz.date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.thedocs.soyuz.to;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Created by fbelov on 17.04.17.
 */
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class LocalDateAsInt {
    private int value;

    @JsonValue
    public int asInt() {
        return value;
    }

    @JsonCreator
    public LocalDateAsInt(int value) {
        this.value = value;
    }

    public LocalDateAsInt(LocalDate date) {
        this.value = date.getYear() * 10000 + date.getMonthValue() * 100 + date.getDayOfMonth();
    }

    public LocalDateAsInt(LocalDateTime date) {
        this(date.toLocalDate());
    }

    public LocalDateAsInt(String value) {
        this(to.Integer(value));
    }

    public LocalDateTime toBeginningOfTheDay() {
        return LocalDateTime.of(toLocalDate(), LocalTime.MIN);
    }

    public LocalDateTime toEndOfTheDay() {
        return LocalDateTime.of(toLocalDate(), LocalTime.MAX);
    }

    public LocalDate toLocalDate() {
        int year = value / 10000;
        int month = (value % 10000) / 100;
        int day = value % 100;

        return LocalDate.of(year, month, day);
    }

    public static LocalDateAsInt today() {
        return new LocalDateAsInt(LocalDate.now());
    }
}
