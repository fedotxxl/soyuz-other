package io.belov.soyuz.io;

import java.io.File;

/**
 * Created by fbelov on 07.06.15.
 */
public interface TempFileProvider {

    File createTempFile();

    File createTempFile(String directoryName);

    File createTempDirectory();

}