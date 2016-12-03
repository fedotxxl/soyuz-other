package io.belov.soyuz.io.temp;

import io.belov.soyuz.io.DaFileUtils;
import io.belov.soyuz.io.filter.CreatedOlderThanFileFilter;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Created by fbelov on 29.05.15.
 */
public class ScheduledTempFileProvider implements TempFileProvider {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTempFileProvider.class);

    private SimpleTempFileProvider simpleTempFileProvider;
    private int lifetimeInSeconds;
    private FileDeleter fileDeleter;

    public ScheduledTempFileProvider(String rootDirName, String filePrefix, int lifetimeInSeconds) {
        this(rootDirName, filePrefix, lifetimeInSeconds, false);
    }

    public ScheduledTempFileProvider(String rootDirName, String filePrefix, int lifetimeInSeconds, boolean isDeleteDirectories) {
        this.simpleTempFileProvider = new SimpleTempFileProvider(rootDirName, filePrefix);
        this.lifetimeInSeconds = lifetimeInSeconds;
        this.fileDeleter = periodicallyCheckTempDirectoryAndDeleteFiles(isDeleteDirectories);
    }

    public void interrupt() {
        fileDeleter.interrupt();
    }

    @Override
    public File createTempFile() {
        return simpleTempFileProvider.createTempFile();
    }

    @Override
    public File createTempFile(String directoryName) {
        return simpleTempFileProvider.createTempFile(directoryName);
    }

    @Override
    public File createTempDirectory() {
        return simpleTempFileProvider.createTempDirectory();
    }

    private FileDeleter periodicallyCheckTempDirectoryAndDeleteFiles(boolean isDeleteDirectories) {
        IOFileFilter fileFilterByName = new PrefixFileFilter(simpleTempFileProvider.getFilePrefix());
        IOFileFilter fileFilterByAge = new CreatedOlderThanFileFilter(lifetimeInSeconds, TimeUnit.SECONDS);
        IOFileFilter fileFilter = new AndFileFilter(fileFilterByName, fileFilterByAge);
        IOFileFilter dirFilter = (isDeleteDirectories) ? fileFilter : FalseFileFilter.INSTANCE;
        long delayInMillis = lifetimeInSeconds * 333;
        FileDeleter deleter = new FileDeleter(simpleTempFileProvider.getRootDir(), fileFilter, dirFilter, delayInMillis);

        Thread thread = new Thread(deleter);
        thread.setDaemon(true);
        thread.start();

        return deleter;
    }

    private static class FileDeleter implements Runnable {

        private File dir;
        private IOFileFilter fileFilter;
        private IOFileFilter dirFilter;
        private long delayInMillis;
        private volatile boolean active = true;

        public FileDeleter(File dir, IOFileFilter fileFilter, IOFileFilter dirFilter, long delayInMillis) {
            this.dir = dir;
            this.fileFilter = fileFilter;
            this.dirFilter = dirFilter;
            this.delayInMillis = delayInMillis;
        }

        public void interrupt() {
            active = false;
        }

        @Override
        public void run() {
            try {
                log.info("Start deleting temporary files from {}", dir);

                while (active) {
                    deleteFiles();
                    sleep();
                }
            } catch (Exception e) {
                log.error("Exception on deleting temporary files from " + dir.getAbsolutePath(), e);
            }
        }

        private void deleteFiles() {
            DaFileUtils.DeletionResult result = DaFileUtils.deleteFiles(dir, fileFilter, dirFilter);

            if (result.isDeletedSomething()) {
                log.debug("Deleted {}/{} temporary dirs/files", result.getDirsCount(), result.getFilesCount());
            }
        }

        private void sleep() {
            try {
                TimeUnit.MILLISECONDS.sleep(delayInMillis);
            } catch (InterruptedException e) {
            }
        }
    }
}
