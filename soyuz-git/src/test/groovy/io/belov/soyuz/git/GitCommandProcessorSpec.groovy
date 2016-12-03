package io.belov.soyuz.git

import io.belov.soyuz.utils.exec.CollectingLogListener
import spock.lang.Specification

/**
 * Created by fbelov on 18.11.15.
 */
class GitCommandProcessorSpec extends Specification {

    def "should checkout part of the repository"() {
        setup:
        def url = "git@github.com:dadmin/other.git"
        def sshKey = new File("/home/fbelov/tmp/ssh/id_rsa")
        def targetDir = new File("/tmp/git-checkout/test/")
        def sparseDir = "spider/src/main/groovy"
        def branch = null
        def listener = new CollectingLogListener()

        when:
        def answer = Context.gitCommandProcessor.initAndPull(url, targetDir, sshKey, branch, sparseDir, listener)

        then:
        assert answer == 0
        assert listener.get()
    }

    def "should check connection to remote repository"() {
        setup:
        def repositoryDir = new File(repositoryPath)
        def sshKey = new File("/home/fbelov/tmp/ssh/id_rsa")
        def listener = new CollectingLogListener()

        when:
        def result = Context.gitCommandProcessor.checkConnectionToRemote(repositoryDir, sshKey, listener)

        then:
        assert listener.get()
        assert result == 0

        where:
        repositoryPath << ["/home/fbelov/job/projects-active/thedocs/"]
    }
}
