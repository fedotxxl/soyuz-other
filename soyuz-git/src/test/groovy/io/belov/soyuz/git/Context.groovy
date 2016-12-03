package io.belov.soyuz.git

import io.belov.soyuz.io.temp.SimpleTempFileProvider
import io.belov.soyuz.io.temp.TempFileProvider

/**
 * Created by fbelov on 15.11.15.
 */
class Context {
    static final TempFileProvider tempFileProvider = new SimpleTempFileProvider("thedocs-git")
    static final GitCommandProcessor gitCommandProcessor = new GitCommandProcessor(tempFileProvider.createTempDirectory(), 10, 10)
    static final GitManager gitManager = new GitManager(gitCommandProcessor, tempFileProvider)

    static {
        gitCommandProcessor.prepareBinRoot()
    }
}
