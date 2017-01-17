package io.belov.soyuz.db.jooq.spring;

import org.jooq.TransactionContext;
import org.jooq.TransactionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.ResourceTransactionManager;

import static org.springframework.transaction.TransactionDefinition.PROPAGATION_NESTED;

/**
 * An example <code>TransactionProvider</code> implementing the
 * {@link TransactionProvider} contract for use with Spring.
 *
 * @author Lukas Eder
 */
public class SpringTransactionProvider implements TransactionProvider {

    @Autowired
    ResourceTransactionManager txMgr;

    @Override
    public void begin(TransactionContext ctx) {
        // This TransactionProvider behaves like jOOQ's DefaultTransactionProvider,
        // which supports nested transactions using Savepoints
        TransactionStatus tx = txMgr.getTransaction(new DefaultTransactionDefinition(PROPAGATION_NESTED));
        ctx.transaction(new SpringTransaction(tx));
    }

    @Override
    public void commit(TransactionContext ctx) {
        txMgr.commit(((SpringTransaction) ctx.transaction()).tx);
    }

    @Override
    public void rollback(TransactionContext ctx) {
        txMgr.rollback(((SpringTransaction) ctx.transaction()).tx);
    }
}
