package io.belov.soyuz.utils.exec;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created by fbelov on 08.11.15.
 */
public class KillableExecutor extends DefaultExecutor {

    protected Process launch(final CommandLine command, final Map<String, String> env, final File dir) throws IOException {
        return new KillableProcess(super.launch(command, env, dir));
    }
}
