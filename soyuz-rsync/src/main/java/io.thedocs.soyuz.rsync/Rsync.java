package io.thedocs.soyuz.rsync;

import io.belov.soyuz.utils.exec.CollectingLogOutputStream;
import io.belov.soyuz.utils.exec.KillableExecutor;
import io.belov.soyuz.utils.is;
import io.belov.soyuz.utils.to;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.exec.*;
import org.apache.commons.validator.Arg;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import static org.apache.commons.exec.ExecuteWatchdog.INFINITE_TIMEOUT;

/**
 * Created on 28.10.17.
 */
public class Rsync {
    private String source;
    private String destination;
    private List<Argument> arguments;
    private Consumer<String> listenerOut;
    private Consumer<String> listenerErr;

    public Rsync source(String source) {
        this.source = source;

        return this;
    }

    public Rsync destination(String destination) {
        this.destination = destination;

        return this;
    }

    public Rsync arguments(String... arguments) {
        this.arguments = to.list(arguments, a -> new Argument(a, true));

        return this;
    }

    public Rsync arguments(Argument... arguments) {
        this.arguments = to.list(arguments);

        return this;
    }

    public Rsync arguments(List<String> arguments) {
        this.arguments = to.list(arguments, a -> new Argument(a, true));

        return this;
    }

    public Rsync arguments(Collection<Argument> arguments) {
        this.arguments = to.list(arguments);

        return this;
    }

    public Rsync listenerOut(Consumer<String> listenerOut) {
        this.listenerOut = listenerOut;

        return this;
    }

    public Rsync listenerErr(Consumer<String> listenerErr) {
        this.listenerErr = listenerErr;

        return this;
    }

    public Result execute() {
        return execute(-1);
    }

    @SneakyThrows
    public Result execute(long timeoutInMillis) {
        if (!is.t(source)) throw new IllegalStateException("Source should be specified");
        if (!is.t(destination)) throw new IllegalStateException("Destination should be specified");

        CommandLine cmdLine = new CommandLine("rsync");

        for (Argument argument : arguments) {
            cmdLine.addArgument(argument.getValue(), argument.isHandleQuoting());
        }

        cmdLine.addArgument(source);
        cmdLine.addArgument(destination);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        CollectingLogOutputStream logOutputStreamOut = new CollectingLogOutputStream(listenerOut);
        CollectingLogOutputStream logOutputStreamErr = new CollectingLogOutputStream(listenerErr);
        PumpStreamHandler psh = new PumpStreamHandler(logOutputStreamOut, logOutputStreamErr);
        ExecuteWatchdog watchdog = new ExecuteWatchdog((timeoutInMillis <= 0) ? INFINITE_TIMEOUT : timeoutInMillis);
        Executor exec = new KillableExecutor();
        exec.setWatchdog(watchdog); //implement killing watchdog - http://stackoverflow.com/questions/2950338/how-can-i-kill-a-linux-process-in-java-with-sigkill-process-destroy-does-sigte
        exec.setStreamHandler(psh);
        exec.execute(cmdLine, resultHandler);

        resultHandler.waitFor();

        return new Result(resultHandler.getExitValue(), logOutputStreamOut.getLines(), logOutputStreamErr.getLines());
    }

    @AllArgsConstructor
    @Getter
    public static class Result {
        private int exitValue;
        private List<String> out;
        private List<String> err;
    }

    @AllArgsConstructor
    @Getter
    public static class Argument {
        private String value;
        private boolean handleQuoting;
    }
}
