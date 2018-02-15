package io.thedocs.soyuz.db.jooq.crud;

/**
 * Добавляет методы, необходимые для crud операций
 */
public interface CrudBeanI<I> {

    interface Int extends CrudBeanI<Integer> {
    }

    interface Long extends CrudBeanI<Long> {
    }

    I getId();

}
