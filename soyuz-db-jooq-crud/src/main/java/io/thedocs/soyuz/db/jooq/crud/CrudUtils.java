package io.thedocs.soyuz.db.jooq.crud;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created on 17.08.17.
 */
class CrudUtils {

    private static final ExecutorService pool = Executors.newCachedThreadPool();

    static ExecutorService getDefaultPool() {
        return pool;
    }

}
