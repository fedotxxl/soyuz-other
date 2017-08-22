package io.thedocs.soyuz.db.jooq.crud;

import org.jooq.Record;

/**
 * Добавляет операции чтения и записи в сервис
 */
public interface CrudWriteJoinDaoI<T extends CrudBeanI<?>, LR extends JooqListRequestI> extends CrudJoinDaoI<T, LR>, CrudDaoI<T, Record, LR> {
}
