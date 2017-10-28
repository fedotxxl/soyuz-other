package io.thedocs.soyuz.rsync;

import io.belov.soyuz.utils.is;
import io.belov.soyuz.utils.to;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.*;

/**
 * Created by fbelov on 28.10.17.
 */
@Ignore
public class RsyncTest {

    @Test
    public void shouldRun() {
        //when
        Rsync rsync = new Rsync();

        rsync
                .source("root@thedocs.io:/home/fbelov/tmp/")
                .destination("/home/fbelov/tmp/rsync-test/")
                .arguments("-avlzp", "--delete");

        Rsync.Result result = rsync.execute();

        //then
        assertEquals(result.getExitValue(), 0);
        assertTrue(is.t(result.getOut()));
        assertFalse(is.t(result.getErr()));
    }

    @Test
    public void shouldCallListeners() {
        //when
        List<String> out = to.list();
        Rsync rsync = new Rsync();

        rsync
                .source("root@thedocs.io:/home/fbelov/tmp/")
                .destination("/home/fbelov/tmp/rsync-test/")
                .arguments("-avlzp", "--delete")
                .listenerOut(out::add);

        Rsync.Result result = rsync.execute();

        //then
        assertEquals(result.getExitValue(), 0);
        assertTrue(is.t(result.getOut()));
        assertEquals(result.getOut(), out);
        assertFalse(is.t(result.getErr()));
    }

    @Test
    public void shouldStopByTimeout() {
        //setup
        long started = System.currentTimeMillis();

        //when
        Rsync rsync = new Rsync();

        rsync
                .source("root@thedocs.io:/home/fbelov/tmp/")
                .destination("/home/fbelov/tmp/rsync-test/")
                .arguments("-avlzp", "--delete");

        Rsync.Result result = rsync.execute(SECONDS.toMillis(1));

        //then
        assertNotEquals(result.getExitValue(), 0);
        assertTrue(is.t(result.getErr()));
        assertTrue(System.currentTimeMillis() - started < SECONDS.toMillis(2));
    }

}