package io.thedocs.soyuz.db.jooq.crud;

/**
 * Сервис - чтение + запись.
 * @see CrudReadServiceI - чтение
 * @see CrudWriteServiceI - запись
 */
public interface CrudServiceI<T extends CrudBeanI<I>, I, D extends CrudDaoI<T, I, LR>, LR extends JooqListRequestI> extends CrudReadServiceI<T, I, D, LR>, CrudWriteServiceI<T, I, D, LR> {

    interface Int<T extends CrudBeanI<Integer>, D extends CrudDaoI<T, Integer, LR>, LR extends JooqListRequestI> extends CrudServiceI<T, Integer, D, LR> {
    }

    interface Long<T extends CrudBeanI<Long>, D extends CrudDaoI<T, Long, LR>, LR extends JooqListRequestI> extends CrudServiceI<T, Long, D, LR> {
    }

}

