package io.thedocs.soyuz.db.jooq.crud;

/**
 * Listener'ы операций изменения
 */
public interface CrudListenerI {
    interface PostUpdate<T extends CrudBeanI> {
        default void postUpdate(T object) {}
    }

    interface PostDelete<T extends CrudBeanI, I> {

        interface Int<T extends CrudBeanI.Int> extends PostDelete<T, Integer> {
        }

        default void postDelete(I id) {}
    }
}
