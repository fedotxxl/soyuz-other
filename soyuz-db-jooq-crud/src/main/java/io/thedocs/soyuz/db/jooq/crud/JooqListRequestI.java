package io.thedocs.soyuz.db.jooq.crud;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.thedocs.soyuz.to;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Параметры фильтрации для получения списка объектов (метод list)
 */
public interface JooqListRequestI {

    int getPage();
    int getItemsPerPage();

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

        return new JooqParams(limit, offset, getOrder());
    }

    @Getter
    @AllArgsConstructor
    class JooqParams {
        private int limit;
        private int offset;
        private List<FieldWithOrder> sortFields;

        public boolean hasOffsetAndLimit() {
            return limit >= 0 && offset >= 0;
        }
    }
}
