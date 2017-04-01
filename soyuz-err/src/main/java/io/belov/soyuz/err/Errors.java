package io.belov.soyuz.err;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by fbelov on 31.05.16.
 */
@EqualsAndHashCode
@ToString
public class Errors implements Iterable<Err> {

    private List<Err> errors;

    private Errors(List<Err> errors) {
        this.errors = errors;
    }

    public Errors add(Err... errors) {
        return add(Arrays.asList(errors));
    }

    public Errors add(Collection<Err> errors) {
        this.errors.addAll(errors);

        return this;
    }

    @JsonIgnore
    public boolean isOk() {
        return !hasErrors();
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public List<Err> get() {
        return errors;
    }

    @JsonProperty("global")
    public List<Err> getGlobalErrors() {
        return errors.stream().filter(Err::isGlobalScope).collect(Collectors.toList());
    }

    @JsonProperty("field")
    public List<Err> getFieldErrors() {
        return errors.stream().filter(Err::isFieldScope).collect(Collectors.toList());
    }

    public static Errors ok() {
        return new Errors(new ArrayList<>());
    }

    public static Errors reject(Err... errors) {
        List<Err> answer = new ArrayList<>(errors.length);
        Collections.addAll(answer, errors);
        return new Errors(answer);
    }

    public static Errors reject(Collection<Err> errors) {
        List<Err> answer = new ArrayList<>();
        answer.addAll(errors);
        return new Errors(answer);
    }

    @Override
    public Iterator<Err> iterator() {
        return errors.iterator();
    }
}
