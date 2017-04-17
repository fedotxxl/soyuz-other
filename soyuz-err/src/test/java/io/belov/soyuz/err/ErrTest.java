package io.belov.soyuz.err;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created on 02.04.17.
 */
public class ErrTest {

    @Test
    public void shouldCreateErr() {
        Err err = Err.code("hello").build();
        Err<String> errString = Err.<String>field("field").value("value").build();

        Assert.assertNotNull(err);
        Assert.assertNotNull(errString);
        Assert.assertTrue(errString.getValue() != null);
    }

}