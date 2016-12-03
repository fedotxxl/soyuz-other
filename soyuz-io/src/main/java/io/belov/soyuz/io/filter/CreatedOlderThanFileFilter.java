package io.belov.soyuz.io.filter;

import com.google.common.base.Throwables;
import org.apache.commons.io.filefilter.IOFileFilter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.TimeUnit;

/**
 * Created by fbelov on 13.08.15.
 */
public class CreatedOlderThanFileFilter implements IOFileFilter {

    private long lifetimeInMillis;

    public CreatedOlderThanFileFilter(long lifetime, TimeUnit timeUnit) {
        this.lifetimeInMillis = timeUnit.toMillis(lifetime);
    }

    @Override
    public boolean accept(File file) {
        return isFileTooOld(file);
    }

    @Override
    public boolean accept(File file, String s) {
        return accept(file);
    }

    private boolean isFileTooOld(File file) {
        try {
            BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            FileTime creationTime = attr.creationTime();

            return System.currentTimeMillis() > (creationTime.toMillis() + lifetimeInMillis);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }
}
