package io.belov.soyuz.io.temp;

import java.io.File;

/**
 * Created by fbelov on 16.04.15.
 */
public interface TempFileProvider {

    File createTempFile();

    File createTempFile(String directoryName);

    File createTempDirectory();

}
