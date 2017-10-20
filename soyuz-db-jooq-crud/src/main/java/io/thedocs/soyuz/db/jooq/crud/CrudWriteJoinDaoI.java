package io.thedocs.soyuz.db.jooq.crud;

/**
 * Добавляет операции чтения и записи в сервис
 */
public interface CrudWriteJoinDaoI<T extends CrudBeanI<I>, I, LR extends JooqListRequestI> extends CrudJoinDaoI<T, I, LR>, CrudDaoI<T, I, LR> {

    interface Int<T extends CrudBeanI.Int, LR extends JooqListRequestI> extends CrudWriteJoinDaoI<T, Integer, LR> {
    }

}
