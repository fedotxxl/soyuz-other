package io.belov.soyuz.io.temp;

import com.google.common.base.Throwables;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by fbelov on 16.04.15.
 */
public class SimpleTempFileProvider implements TempFileProvider {

    private File rootDir;
    private String filePrefix;

    public SimpleTempFileProvider(String rootDirName) {
        this(rootDirName, "");
    }

    public SimpleTempFileProvider(String rootDirName, String filePrefix) {
        this.filePrefix = filePrefix;
        this.rootDir = new File(getSystemTempDirectory(), rootDirName);

        if (!this.rootDir.exists() && !this.rootDir.mkdirs()) {
            throw new IllegalStateException("Can't create directory for temp files: " + rootDir.getAbsolutePath());
        }
    }

    public File getRootDir() {
        return rootDir;
    }

    public String getFilePrefix() {
        return filePrefix;
    }

    @Override
    public File createTempFile() {
        return createTempFileInDirectory(null);
    }

    @Override
    public File createTempFile(String directoryName) {
        try {
            return createTempFileInDirectory(directoryName);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public File createTempDirectory() {
        try {
            File file = Files.createTempDirectory(rootDir.toPath(), filePrefix + RandomStringUtils.randomAlphanumeric(10)).toFile();
            file.deleteOnExit();
            return file;
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    private File createTempFileInDirectory(String directory) {
        try {
            Path path = (directory == null) ? rootDir.toPath() : new File(rootDir, directory).toPath();
            path.toFile().mkdirs();
            File file = Files.createTempFile(path, filePrefix + RandomStringUtils.randomAlphanumeric(10), null).toFile();
            file.deleteOnExit();
            return file;
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    private File getSystemTempDirectory() {
        String path = System.getProperty("java.io.tmpdir");
        File tempDirectory = new File(path);

        tempDirectory.mkdirs();

        return tempDirectory;
    }
}
