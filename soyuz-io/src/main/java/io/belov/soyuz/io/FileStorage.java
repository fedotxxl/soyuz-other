package io.belov.soyuz.io;

import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * Created by fbelov on 15.02.16.
 */
public class FileStorage {

    private File dir;

    public FileStorage(String dirPath) {
        this(new File(dirPath));
    }

    public FileStorage(File dir) {
        this.dir = dir;
        DaFileUtils.mkdirsOrLog(dir);
    }

    public boolean clean() {
        boolean answer = delete();

        DaFileUtils.mkdirsOrLog(dir);

        return answer;
    }

    public File getDir() {
        return dir;
    }

    public File getFile(String fileName) {
        return new File(dir, fileName);
    }

    public File getSafeFileOrNull(String path) {
        return DaFileUtils.getSafeFileOrNull(dir, path);
    }

    public boolean delete() {
        return DaFileUtils.deleteDirectoryOrLog(dir);
    }

    public long getSizeInBytes() {
        return (dir.exists()) ? FileUtils.sizeOfDirectory(dir) : 0;
    }
}
