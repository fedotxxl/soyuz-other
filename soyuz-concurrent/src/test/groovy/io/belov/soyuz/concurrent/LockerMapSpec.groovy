package io.belov.soyuz.concurrent

import org.apache.commons.lang3.RandomStringUtils
import spock.lang.Specification

import java.util.concurrent.CountDownLatch

/**
 * Created by fbelov on 22.11.15.
 */
class LockerMapSpec extends Specification {

    def "should create only single lock per key"() {
        setup:
        def r = new Random()
        def lockers = new LockerMap<String>()
        def locksByKey = [:].withDefault {[] as Set}
        def keys = []
        def keysCount = 10
        def threadsCount = 5000
        def latch = new CountDownLatch(threadsCount)

        keysCount.times {
            keys << RandomStringUtils.randomAscii(10)
        }

        when:
        threadsCount.times {
            Thread.start {
                def key = keys[r.nextInt(keysCount)]
                def lock = lockers.get(key)

                locksByKey[key] << lock
                latch.countDown()
            }
        }

        latch.await()

        then:
        for (def value : locksByKey.values()) {
            assert value.size() <= 1
        }
    }
    private LockerMap<String> lockers = new LockerMap<>();

}
