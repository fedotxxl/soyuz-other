package io.belov.soyuz.route

import spock.lang.Specification

/**
 * Created by fbelov on 01.04.15.
 */
class RouterSpec extends Specification {

    def "should correctly route stackCare.com urls"() {
        setup:
        def chackCareRoutes = """
var vRoutes = {
    projects: {
        list: "/p/",
        settings: "/p/:projectId/settings"
    },
    exceptions: {
        list: "/p/:projectId/"
    },
    user: {
        settings: "/u/"
    }
};
"""
        expect:
        assert getRouterFromString(chackCareRoutes).getRoutePath(url) == path

        where:
        url                                    | path
        "/"                                    | null
        "/a"                                   | null
        "/p/"                                  | "projects.list"
        "/p/c:hello/w:world/"                  | "projects.list"
        "/p/123/a:param/?abc=false"            | "exceptions.list"
        "/p/123/config"                        | null
        "/p/123/config/a:param/"               | null
        "/p/123/settings"                      | "projects.settings"
        "/p/123/settings/b:value/c:filter?a=b" | "projects.settings"
    }


    def "should correctly route wav.tv urls"() {
        setup:
        def wavRoutes = """
var vRoutes = {
    movies: {
        list: "/",
        item: "/m/:id/"
    },
    tags: {
        list: "/tags/"
    },
    actresses: {
        list: "/actresses/",
        item: "/actress/:id/"
    },
    dvds: {
        list: "/dvds/",
        item: "/dvd/:id/"
    }
};
"""
        expect:
        assert getRouterFromString(wavRoutes).getRoutePath(url) == path

        where:
        url                                    | path
    }

    private getRouterFromString(String routes) {
        return new Router(new StringReader(routes))
    }

    private getClassPathInputStream(String path) {
        return this.getClass().getClassLoader().getResourceAsStream(path);
    }

}
