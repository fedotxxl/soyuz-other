package ssh

import spock.lang.Specification

/**
 * Created by fbelov on 03.02.16.
 */
class SshKeyGeneratorSpec extends Specification {

    private SshKeyGenerator sshKeyGenerator = new SshKeyGenerator()

    def "should generate private/public keys"() {
        when:
        def pair = sshKeyGenerator.generate()

        then:
        assert pair.keyPrivate
        assert pair.keyPublic
    }

    def "should generate unique pairs"() {
        when:
        def pairs = (1..1000).collect { sshKeyGenerator.generate() }

        then:
        def privateKeys = pairs.collect { it.keyPrivate } as Set
        def publicKeys = pairs.collect { it.keyPublic } as Set

        assert privateKeys.size() == pairs.size()
        assert publicKeys.size() == pairs.size()
    }

}
