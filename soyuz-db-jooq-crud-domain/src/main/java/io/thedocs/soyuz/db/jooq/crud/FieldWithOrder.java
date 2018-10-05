package io.thedocs.soyuz.db.jooq.crud;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import io.thedocs.soyuz.is;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Параметры сортировки
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FieldWithOrder {
    private String field;
    private String order;

    @JsonCreator
    public FieldWithOrder(String order) {
        if (is.t(order)) {
            if (order.startsWith("-")) {
                this.field = order.substring(1);
                this.order = "DESC";
            } else {
                this.field = order;
                this.order = "ASC";
            }
        }
    }

    @JsonValue
    public String asString() {
        return ((isDesc()) ? "-" : "") + field;
    }

    @JsonIgnore
    public boolean isDesc() {
        return this.order.equalsIgnoreCase("desc");
    }

    @JsonIgnore
    public boolean isAsc() {
        return this.order.equalsIgnoreCase("asc");
    }
}
