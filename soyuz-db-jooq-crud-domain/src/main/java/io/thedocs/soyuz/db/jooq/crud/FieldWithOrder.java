package io.thedocs.soyuz.db.jooq.crud;

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
}
