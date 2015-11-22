package io.belov.soyuz.crypto

import groovy.util.logging.Slf4j
import spock.lang.Specification

/**
 * Created by fbelov on 22.11.15.
 */
@Slf4j
class SshPublicPrivateKeysGeneratorSpec extends Specification {

    def "should generate private / public keys"() {
        setup:
        def generator = new SshPublicPrivateKeysGenerator()

        when:
        def keys = generator.generate(algorithm, numBits)

        then:
        log.info("Generated {}:{} - {}", algorithm, numBits, keys)

        assert keys.keyPublic
        assert keys.keyPrivate

        where:
        algorithm | numBits
        "DSA"     | 1024
        "DH"      | 576
        "RSA"     | 4096 //https://help.github.com/articles/generating-ssh-keys/
    }

}
