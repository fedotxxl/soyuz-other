package io.belov.soyuz.spring.jdbc;

import lombok.SneakyThrows;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.Callable;

/**
 * Created on 08.05.17.
 */
public class TransactionManagerWrapper {

    private TransactionTemplate template;

    public TransactionManagerWrapper(PlatformTransactionManager transactionManager, TransactionDefinition transactionDefinition) {
        this.template = new TransactionTemplate(transactionManager, transactionDefinition);
    }

    public <T> T execute(TransactionCallback<T> action) throws TransactionException {
        return template.execute(action);
    }

    public void execute(Runnable runnable) throws TransactionException {
        template.execute(s -> {
            runnable.run();

            return 0;
        });
    }

    public <T> T execute(Callable<T> callable) throws TransactionException {
        return template.execute(new TransactionCallback<T>() {
            @Override
            @SneakyThrows
            public T doInTransaction(TransactionStatus status) {
                return callable.call();
            }
        });
    }
}
