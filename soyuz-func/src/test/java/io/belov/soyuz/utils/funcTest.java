package io.belov.soyuz.utils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by fbelov on 21.11.16.
 */
public class funcTest {

    @Test
    public void shouldCall() {
        int a = 2;
        int b = 1;

        assertTrue(1 + func.call(() -> a - b) == 2);
    }

}