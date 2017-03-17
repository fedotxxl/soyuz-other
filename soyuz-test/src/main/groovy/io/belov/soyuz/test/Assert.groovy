package io.belov.soyuz.test

/**
 * Created by fbelov on 12.01.16.
 */
class Assert {

    public static boolean asSets(Collection a, Collection b) {
        assert (a as Set) == (b as Set)

        return true
    }

}
