package io.thedocs.soyuz.db.jooq.crud;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.thedocs.soyuz.is;
import io.thedocs.soyuz.to;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jooq.Field;
import org.jooq.SortField;
import org.jooq.SortOrder;
import org.jooq.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Параметры фильтрации для получения списка объектов (метод list)
 */
public interface JooqListRequestI {

    int getPage();
    int getItemsPerPage();
    Table getTable();

    default List<FieldWithOrder> getOrder() {
        return to.list();
    }

    default boolean isShouldJoinToManyData() {
        return true;
    }

    default boolean isNoOffsetsAndLimits() {
        return false;
    }

    default boolean isSkipTotal() {
        return false;
    }

    @Getter
    @AllArgsConstructor
    class JooqParams {
        private int limit;
        private int offset;
        private List<SortField<?>> sortFields;

        public boolean hasOffsetAndLimit() {
            return limit >= 0 && offset >= 0;
        }
    }

    default Field<?> toJooqField(String field) {
        Field<?> answer = getTable().field(field);

        if (answer == null) {
            throw new IllegalStateException("Unable to get field for " + field);
        } else {
            return answer;
        }
    }

    @JsonIgnore
    default JooqParams getJooq() {
        int limit;
        int offset;
        int page = getPage();

        if (isNoOffsetsAndLimits() || page <= 0) {
            offset = -1;
            limit = -1;
        } else {
            limit = getItemsPerPage();
            offset = (page - 1) * limit;
        }

        List<FieldWithOrder> order = getOrder();
        List<SortField<?>> sortFields = !is.t(order) ? new ArrayList<>() : to.list(getOrder(), (o) -> toJooqField(o.getField()).sort((o.getOrder().equalsIgnoreCase("asc")) ? SortOrder.ASC : SortOrder.DESC));

        return new JooqParams(limit, offset, sortFields);
    }
}
