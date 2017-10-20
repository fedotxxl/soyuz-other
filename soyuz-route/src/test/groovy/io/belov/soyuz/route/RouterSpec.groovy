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
        url    | path
        "/m/2" | "movies.item"
    }

    def "should correctly route crud urls"() {
        setup:
        def routes = getClassPathInputStream("urls.crud.js").text

        assert getRouterFromString(routes).getRoutePath(url) == path

        where:
        url                     | path
        "/app/campaigns/"       | "app.campaigns.list"
        "/app/campaigns/7"      | "app.campaigns.item.show"
        "/app/campaigns/new"    | "app.campaigns.item.create"
        "/app/campaigns/7/edit" | "app.campaigns.item.edit"
    }

    private getRouterFromString(String routes) {
        return new Router(new StringReader(routes))
    }

    private getClassPathInputStream(String path) {
        return this.getClass().getClassLoader().getResourceAsStream(path);
    }

}
