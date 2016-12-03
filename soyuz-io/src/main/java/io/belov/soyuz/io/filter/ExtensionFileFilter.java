package io.belov.soyuz.io.filter;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by fbelov on 05.04.16.
 */
public class ExtensionFileFilter implements IOFileFilter {

    private Set<String> extensions;

    public ExtensionFileFilter(@Nullable Collection<String> extensions) {
        if (extensions != null) {
            this.extensions = extensions
                    .stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
        }
    }

    @Override
    public boolean accept(File pathname) {
        return accept(pathname.getName());
    }

    @Override
    public boolean accept(File dir, String name) {
        return accept(name);
    }

    private boolean accept(String fileName) {
        if (extensions == null) {
            return true;
        } else {
            return extensions.contains(FilenameUtils.getExtension(fileName).toLowerCase());
        }
    }
}
