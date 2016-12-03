package io.belov.soyuz.io;

import java.io.File;

/**
 * Created by fbelov on 09.09.15.
 */
public class FileWithMd5 {

    private File file;
    private String md5;

    public FileWithMd5(File file, String md5) {
        this.file = file;
        this.md5 = md5;
    }

    public File getFile() {
        return file;
    }

    public String getMd5() {
        return md5;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileWithMd5 that = (FileWithMd5) o;

        if (!file.equals(that.file)) return false;
        return md5.equals(that.md5);

    }

    @Override
    public int hashCode() {
        int result = file.hashCode();
        result = 31 * result + md5.hashCode();
        return result;
    }
}
