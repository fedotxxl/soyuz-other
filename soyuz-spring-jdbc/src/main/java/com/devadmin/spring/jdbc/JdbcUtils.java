package com.devadmin.spring.jdbc;

import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Timestamp;
import java.util.function.Consumer;

/**
 * Created by fbelov on 27.11.15.
 */
public class JdbcUtils {

    public static Timestamp timestampNow() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static <T> T withNewTransaction(TransactionTemplate transactionTemplate, TransactionCallback<T> execute) {
        int propagationBehavior = -1;

        try {
            propagationBehavior = transactionTemplate.getPropagationBehavior();
            transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

            return transactionTemplate.execute(execute);
        } finally {
            if (propagationBehavior != -1) {
                transactionTemplate.setPropagationBehavior(propagationBehavior);
            }
        }
    }

    public static void withNewTransaction(TransactionTemplate transactionTemplate, Consumer<TransactionStatus> execute) {
        withNewTransaction(transactionTemplate, status -> {
            execute.accept(status);

            return null;
        });
    }

}
