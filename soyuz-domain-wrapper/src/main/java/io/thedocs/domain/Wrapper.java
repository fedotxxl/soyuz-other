package io.thedocs.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import io.thedocs.soyuz.TruthyCastableI;
import io.thedocs.soyuz.is;
import io.thedocs.soyuz.to;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Created on 29.07.19.
 */
public interface Wrapper {

    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    abstract class Uuid implements TruthyCastableI {

        protected UUID id;

        public Uuid(String id) {
            this.id = UUID.fromString(id);
        }

        @JsonValue
        public String asString() {
            return to.nullOr(id, UUID::toString);
        }

        public UUID asUuid() {
            return id;
        }

        @Override
        public String toString() {
            return to.s(id);
        }

        @Override
        public boolean asTruthy() {
            return id != null;
        }
    }

    @EqualsAndHashCode
    @NoArgsConstructor
    abstract class Str implements TruthyCastableI {

        protected String id;

        public Str(String id) {
            this.id = id;
        }

        @JsonValue
        public String asString() {
            return id;
        }

        @Override
        public String toString() {
            return to.s(id);
        }

        @Override
        public boolean asTruthy() {
            return is.t(id);
        }
    }

    abstract class StrLowerCase extends Str {
        public StrLowerCase() {
        }

        public StrLowerCase(String id) {
            super(to.nullOr(id, String::toLowerCase));
        }
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @NoArgsConstructor
    abstract class Long implements TruthyCastableI {

        protected long id;

        public Long(String id) {
            this.id = to.Long(id);
        }

        @JsonValue
        public long asLong() {
            return id;
        }

        @Override
        public String toString() {
            return to.s(id);
        }

        @Override
        public boolean asTruthy() {
            return id != 0;
        }
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @NoArgsConstructor
    abstract class Int implements TruthyCastableI {

        protected int value;

        public Int(String value) {
            this.value = to.Integer(value);
        }

        @JsonValue
        public int asInt() {
            return value;
        }

        @Override
        public String toString() {
            return to.s(value);
        }

        @Override
        public boolean asTruthy() {
            return value != 0;
        }
    }

}
