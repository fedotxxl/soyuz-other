package io.belov.soyuz.db.jooq.spring;

import org.jooq.Transaction;

import org.springframework.transaction.TransactionStatus;

/**
 * https://github.com/jOOQ/jOOQ/tree/master/jOOQ-examples/jOOQ-spring-example
 */
class SpringTransaction implements Transaction {
    final TransactionStatus tx;

    SpringTransaction(TransactionStatus tx) {
        this.tx = tx;
    }
}
