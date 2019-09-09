package io.belov.soyuz.log;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import slothlog.annotation.LogInfo;

/**
 * Created on 29.04.19.
 */
public class LogTestClass {

    private static final Logger log = LoggerFactory.getLogger(LogTestClass.class);

    public static void main(String[] args) {
        LogTestClass a = new LogTestClass();

        a.hello("a", "b");
    }

    public String hello(String name, String sex) {
        log.warn("abc");

        doSomething(new Book(name));

        return name + "-" + sex;
    }

    @LogInfo
    private String doSomething(Book book) {
        doBook(book);
        
        return book.getTitle();
    }

    @LogInfo
    private String doBook(Book book) {
        return book.getTitle() + "abc";
    }

    @ToString
    @AllArgsConstructor
    @Getter
    private static class Book {
        private String title;
    }
}
