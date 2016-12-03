package io.belov.soyuz.web

import spock.lang.Specification

/**
 * Created by fbelov on 03.01.16.
 */
class UrlUtilsSpec extends Specification {

    def "should normalize path"() {
        expect:
        assert UrlUtils.normalizePath(path) == pathNormalized

        where:
        path | pathNormalized
        "a/b c" | "a/b_c"
        "/another Query/yep!/" | "/another_Query/yep_/"
    }

}
