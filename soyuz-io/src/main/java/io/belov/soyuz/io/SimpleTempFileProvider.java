package io.belov.soyuz.io;

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

    public SimpleTempFileProvider(String rootDirName, String filePrefix) {
        try {
            File temp = File.createTempFile("temp-file-name", ".tmp");
            temp.deleteOnExit();

            this.filePrefix = filePrefix;
            this.rootDir = new File(temp.getParentFile(), rootDirName);
            this.rootDir.mkdirs();
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    public File getRootDir() {
        return rootDir;
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
}
