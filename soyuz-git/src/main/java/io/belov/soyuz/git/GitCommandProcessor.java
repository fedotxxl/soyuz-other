package io.belov.soyuz.git;

import com.google.common.base.Throwables;
import io.belov.soyuz.io.DaFileUtils;
import io.belov.soyuz.utils.ClassPathFile;
import io.belov.soyuz.utils.exec.CollectingLogOutputStream;
import io.belov.soyuz.utils.exec.KillableExecutor;
import io.thedocs.soyuz.to;
import org.apache.commons.exec.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by fbelov on 18.11.15.
 */
public class GitCommandProcessor {

    private static final Logger log = LoggerFactory.getLogger(GitCommandProcessor.class);

    private File binRoot;
    private String gitInitPath;
    private String gitCheckConnectionPath;
    private int initAndPullMaxDurationInSeconds;
    private int checkConnectionToRemoteMaxDurationInSeconds;

    public GitCommandProcessor(File binRoot, int initAndPullMaxDurationInSeconds, int checkConnectionToRemoteMaxDurationInSeconds) {
        this.binRoot = binRoot;
        this.gitInitPath = new File(binRoot, "git-init.sh").getAbsolutePath();
        this.gitCheckConnectionPath = new File(binRoot, "git-check-connection.sh").getAbsolutePath();
        this.initAndPullMaxDurationInSeconds = initAndPullMaxDurationInSeconds;
        this.checkConnectionToRemoteMaxDurationInSeconds = checkConnectionToRemoteMaxDurationInSeconds;
    }

    public void prepareBinRoot() {
        DaFileUtils.mkdirsOrThrow(binRoot);
        copyBinFile(binRoot, "git-ssh.sh");
        copyBinFile(binRoot, "git-init.sh");
        copyBinFile(binRoot, "git-check-connection.sh");
    }

    private String copyBinFile(File root, String fileName) {
        try {
            File file = new File(root, fileName);
            DaFileUtils.deleteOrLog(file);
            Files.copy(ClassPathFile.asStream(fileName), file.toPath());
            DaFileUtils.setExecutableOrThrow(file);

            return file.getAbsolutePath();
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    public int init(String url, File targetDir, Consumer<String> listener) {
        return doInitAndPull(url, targetDir, null, null, null, listener);
    }

    //-d /tmp/git-checkout/test/ -u git@github.com:dadmin/other.git -s spider/src/main/groovy -k /home/fbelov/tmp/ssh/id_rsa
    public int initAndPull(String url, File targetDir, File sshKey, @Nullable String branch, @Nullable String sparseDir, Consumer<String> listener) {
        return doInitAndPull(url, targetDir, sshKey, branch, sparseDir, listener);
    }

    private int doInitAndPull(String url, File targetDir, @Nullable File sshKey, @Nullable String branch, @Nullable String sparseDir, Consumer<String> listener) {
        try {
            CommandLine cmdLine = new CommandLine(gitInitPath);
            cmdLine.addArgument("-d");
            cmdLine.addArgument("${dir}");
            cmdLine.addArgument("-u");
            cmdLine.addArgument("${url}");
            cmdLine.addArgument("-b");
            cmdLine.addArgument("${branch}");
            cmdLine.addArgument("-s");
            cmdLine.addArgument("${sparse}");
            cmdLine.addArgument("-k");
            cmdLine.addArgument("${key}");

            Map<String, String> map = new HashMap<>();
            map.put("dir", targetDir.getAbsolutePath());
            map.put("url", url);
            map.put("branch", (branch != null) ? branch : "master");
            map.put("sparse", (sparseDir != null) ? sparseDir : "");
            map.put("key", (sshKey != null) ? sshKey.getAbsolutePath() : "");
            cmdLine.setSubstitutionMap(stripInvalidChars(map));

            DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

            PumpStreamHandler psh = new PumpStreamHandler(new CollectingLogOutputStream(listener, log));
            ExecuteWatchdog watchdog = new ExecuteWatchdog(initAndPullMaxDurationInSeconds * 1000);
            Executor exec = new KillableExecutor();
            exec.setWatchdog(watchdog); //implement killing watchdog - http://stackoverflow.com/questions/2950338/how-can-i-kill-a-linux-process-in-java-with-sigkill-process-destroy-does-sigte
            exec.setStreamHandler(psh);
            exec.execute(cmdLine, resultHandler);

            resultHandler.waitFor();

            return resultHandler.getExitValue();
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    public int checkConnectionToRemote(File repositoryDir, File sshKey, Consumer<String> listener) {
        try {
            CommandLine cmdLine = new CommandLine(gitCheckConnectionPath);
            cmdLine.addArgument("-d");
            cmdLine.addArgument("${dir}");
            cmdLine.addArgument("-k");
            cmdLine.addArgument("${key}");

            Map<String, String> map = new HashMap<>();
            map.put("dir", repositoryDir.getAbsolutePath());
            map.put("key", sshKey.getAbsolutePath());
            cmdLine.setSubstitutionMap(stripInvalidChars(map));

            DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

            PumpStreamHandler psh = new PumpStreamHandler(new CollectingLogOutputStream(listener, log));
            ExecuteWatchdog watchdog = new ExecuteWatchdog(checkConnectionToRemoteMaxDurationInSeconds * 1000);
            Executor exec = new KillableExecutor();
            exec.setWorkingDirectory(repositoryDir);
            exec.setWatchdog(watchdog);
            exec.setStreamHandler(psh);
            exec.execute(cmdLine, resultHandler);

            resultHandler.waitFor();

            return resultHandler.getExitValue();
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    private Map<String, String> stripInvalidChars(Map<String, String> params) {
        return to.map(params, (k, v) -> {
            return stripInvalidChars(v);
        });
    }

    private String stripInvalidChars(String param) {
        return param.replaceAll("\"", "");
    }
}
