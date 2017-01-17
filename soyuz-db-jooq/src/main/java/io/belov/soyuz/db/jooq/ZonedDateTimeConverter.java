package io.belov.soyuz.db.jooq;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.jooq.Converter;

/**
 * https://github.com/cantinac/jooq-example/blob/master/src/main/java/co/cantina/jooq/example/ZonedDateTimeConverter.java
 */
public class ZonedDateTimeConverter implements Converter<Timestamp, ZonedDateTime> {

    private static final long serialVersionUID = 1L;

    @Override
    public ZonedDateTime from(final Timestamp timestamp) {
        return ZonedDateTime.ofInstant(timestamp.toInstant(), ZoneId.systemDefault());
    }

    public ZonedDateTime fromOrNull(final Timestamp timestamp) {
        return (timestamp == null) ? null : from(timestamp);
    }

    @Override
    public Timestamp to(final ZonedDateTime zonedDateTime) {
        return Timestamp.from(zonedDateTime.toInstant());
    }

    public Timestamp toOrNull(final ZonedDateTime zonedDateTime) {
        return (zonedDateTime == null) ? null : to(zonedDateTime);
    }

    @Override
    public Class<Timestamp> fromType() {
        return Timestamp.class;
    }

    @Override
    public Class<ZonedDateTime> toType() {
        return ZonedDateTime.class;
    }
}