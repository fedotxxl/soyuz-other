package io.belov.soyuz.git;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import io.belov.soyuz.utils.is;
import io.belov.soyuz.utils.exec.CollectingLogListener;
import io.belov.soyuz.io.DaFileUtils;
import io.belov.soyuz.io.temp.TempFileProvider;
import lombok.Value;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by fbelov on 07.11.15.
 */
public class GitManager {

    private static final Pattern CONNECTION_REFUSED_PATTERN = Pattern.compile("ssh: connect to host (?:.+) port (?:.+): connection refused");

    private GitCommandProcessor gitCommandProcessor;
    private TempFileProvider tempFileProvider;

    public GitManager(GitCommandProcessor gitCommandProcessor, TempFileProvider tempFileProvider) {
        this.gitCommandProcessor = gitCommandProcessor;
        this.tempFileProvider = tempFileProvider;
    }

    public Result checkConnectionToRemote(String url, String sshKey) {
        if (!is.tt(url) || !is.tt(sshKey)) {
            return Result.failure(RemoteProblem.INCORRECT_INPUT_DATA);
        }

        return writeKeyAndDo(sshKey, (s) -> checkConnectionToRemote(url, s));
    }

    public Result checkConnectionToRemote(String url, File sshKey) {
        if (!is.tt(url)) {
            return Result.failure(RemoteProblem.INCORRECT_INPUT_DATA);
        }

        CollectingLogListener listener;
        int exitCode;
        File repositoryDir = tempFileProvider.createTempDirectory();

        try {
            listener = new CollectingLogListener();
            exitCode = gitCommandProcessor.init(url, repositoryDir, listener);

            if (exitCode != 0) {
                return new Result(exitCode, false, RemoteProblem.UNKNOWN, listener.get());
            } else {
                listener = new CollectingLogListener();
                exitCode = gitCommandProcessor.checkConnectionToRemote(repositoryDir, sshKey, listener);
                List<String> logs = listener.get();

                return new Result(exitCode, exitCode == 0, getRemoteProblem(exitCode, logs), logs);
            }
        } finally {
            DaFileUtils.deleteDirectoryOrLog(repositoryDir);
        }
    }

    public Result initAndPull(String url, String branch, File targetDir, String sshKey, String sparseDir) {
        if (!is.tt(url) || !is.tt(sshKey) || targetDir == null) {
            return Result.failure(RemoteProblem.INCORRECT_INPUT_DATA);
        }

        return writeKeyAndDo(sshKey, (s) -> {
            CollectingLogListener listener = new CollectingLogListener();
            int exitCode = gitCommandProcessor.initAndPull(url, targetDir, s, branch, sparseDir, listener);
            List<String> logs = listener.get();

            return new Result(exitCode, exitCode == 0, getRemoteProblem(exitCode, logs), logs);
        });
    }

    private RemoteProblem getRemoteProblem(int exitCode, List<String> logs) {
        if (exitCode == 0) {
            return null;
        } else if (exitCode == 143) {
            return RemoteProblem.KILLED_BY_WATCHDOG;
        } else {
            List<String> logsLowerCase = logs.stream().map(String::toLowerCase).collect(Collectors.toList());
            String message = String.join("\n", logsLowerCase).toLowerCase();

            if (message.contains("permission denied")) {
                return RemoteProblem.AUTH;
            } else if (message.contains("could not read from remote repository")) {
                return RemoteProblem.AUTH_OR_REPO_DOESNT_EXIST;
            } else if (message.contains("does not appear to be a git repository")) {
                return RemoteProblem.NOT_A_REPO;
            } else if (message.contains("repository does not exist") || message.contains("repository not found")) {
                return RemoteProblem.REPO_DOESNT_EXIST;
            } else if (message.contains("ssh: could not resolve hostname")) {
                return RemoteProblem.CANT_CONNECT;
            } else if (message.contains("couldn't find remote ref") || message.contains("invalid refspec")) {
                return RemoteProblem.INCORRECT_BRANCH;
            } else if (message.contains("sparse checkout leaves no entry on working directory")) {
                return RemoteProblem.INCORRECT_DIR;
            }

            for (String log : logsLowerCase) {
                if (CONNECTION_REFUSED_PATTERN.matcher(log).matches()) {
                    return RemoteProblem.CANT_CONNECT;
                }
            }
        }

        return RemoteProblem.UNKNOWN;
    }

    private <T> T writeKeyAndDo(String sshKey, Function<File, T> function) {
        File sshKeyFile = tempFileProvider.createTempFile();

        try {
            FileUtils.write(sshKeyFile, sshKey, "UTF-8");

            return function.apply(sshKeyFile);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        } finally {
            DaFileUtils.deleteOrLog(sshKeyFile);
        }
    }

    @Value
    public static class Result {
        private boolean isSuccess;
        private int exitCode;
        private List<String> log;
        private RemoteProblem problem;

        public Result(int exitCode, boolean isSuccess) {
            this(exitCode, isSuccess, null);
        }

        public Result(int exitCode, boolean isSuccess, RemoteProblem problem) {
            this(exitCode, isSuccess, problem, null);
        }

        public Result(int exitCode, boolean isSuccess, RemoteProblem problem, List<String> log) {
            this.exitCode = exitCode;
            this.isSuccess = isSuccess;
            this.problem = problem;
            this.log = log;
        }

        public static Result failure(RemoteProblem problem) {
            return new Result(-1, false, problem);
        }
    }

    public enum RemoteProblem {
        AUTH, AUTH_OR_REPO_DOESNT_EXIST, CANT_CONNECT, UNKNOWN, NOT_A_REPO, REPO_DOESNT_EXIST, INCORRECT_INPUT_DATA, INCORRECT_BRANCH, INCORRECT_DIR, KILLED_BY_WATCHDOG
    }
}
