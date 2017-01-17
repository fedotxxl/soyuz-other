package io.belov.soyuz.db.jooq;

import org.jooq.Condition;
import org.jooq.Operator;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * http://www.programania.net/diseno-de-software/functional-trick-to-compose-conditions-in-jooq/
 */
public class Conditions {

    private Operator operator;
    private List<Supplier<Condition>> conditionSuppliers = new ArrayList<>();
    private List<Condition> conditions = new ArrayList<>();

    private Conditions(Operator operator) {
        this.operator = operator;
    }

    public Conditions add(boolean isPresent, Supplier<Condition> conditionSupplier) {
        if (isPresent) conditionSuppliers.add(conditionSupplier);

        return this;
    }

    public Conditions add(boolean isPresent, Condition condition) {
        if (isPresent) conditions.add(condition);

        return this;
    }

    public Conditions add(Supplier<Condition> conditionSupplier) {
        conditionSuppliers.add(conditionSupplier);

        return this;
    }

    public Conditions add(Condition condition) {
        conditions.add(condition);

        return this;
    }

    public Condition get() {
        if (operator == Operator.AND) {
            Condition answer = DSL.trueCondition();

            for (Condition c : conditions) {
                answer = answer.and(c);
            }

            for (Supplier<Condition> c : conditionSuppliers) {
                answer = answer.and(c.get());
            }

            return answer;
        } else if (operator == Operator.OR) {
            Condition answer = DSL.falseCondition();

            for (Condition c : conditions) {
                answer = answer.or(c);
            }

            for (Supplier<Condition> c : conditionSuppliers) {
                answer = answer.or(c.get());
            }

            return answer;
        } else {
            throw new IllegalStateException();
        }
    }

    public static Conditions or() {
        return new Conditions(Operator.OR);
    }

    public static Conditions and() {
        return new Conditions(Operator.AND);
    }
}
