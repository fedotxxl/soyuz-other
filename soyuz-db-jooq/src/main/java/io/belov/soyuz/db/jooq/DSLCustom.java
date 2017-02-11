package io.belov.soyuz.db.jooq;

import org.jooq.Condition;
import org.jooq.Record1;
import org.jooq.Select;
import org.jooq.impl.DSL;

/**
 * Created on 11.02.17.
 */
public interface DSLCustom {

    class Postgres {

        //SELECT u.id FROM juser u WHERE array(SELECT advertiser_id FROM user_advertiser WHERE user_id = u.id) @> ARRAY [213, 6057];
        public static <T> Condition toArrayAndContains(Select<? extends Record1<T>> select, T[] values) {
            return DSL.condition("ARRAY({0}) @> {1}", select, values);
        }

    }

}
