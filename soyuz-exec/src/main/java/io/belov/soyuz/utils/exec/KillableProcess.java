package io.belov.soyuz.utils.exec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by fbelov on 08.11.15.
 */
public class KillableProcess extends Process {

    private static final Logger log = LoggerFactory.getLogger(KillableProcess.class);

    private Process process;

    public KillableProcess(Process process) {
        this.process = process;
    }

    @Override
    public void destroy() {
        int exitValue = UnixProcessUtils.killUnixProcess(process);

        if (exitValue != 0) {
            log.warn("Unable to kill process {} - {}", process, exitValue);
        }
    }

    //DELEGATE METHODS

    @Override
    public OutputStream getOutputStream() {
        return process.getOutputStream();
    }

    @Override
    public InputStream getInputStream() {
        return process.getInputStream();
    }

    @Override
    public InputStream getErrorStream() {
        return process.getErrorStream();
    }

    @Override
    public int waitFor() throws InterruptedException {
        return process.waitFor();
    }

    @Override
    public int exitValue() {
        return process.exitValue();
    }
}
