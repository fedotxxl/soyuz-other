package io.belov.soyuz.template

import spock.lang.Specification

/**
 * Created by fbelov on 07.04.16.
 */
class MustacheTemplateServiceSpec extends Specification {

    private MustacheTemplateService mustacheTemplateService = new MustacheTemplateService()

    def "should compile simple template"() {
        when:
        def product = new Product(name: "car")

        then:
        assert mustacheTemplateService.execute("simple.hbs", product) == "Name: car"
    }

    private static class Order {
        List<Product> items
    }

    private static class Product {
        String name
        int price
        List<Feature> features

        private static class Feature {
            String description
        }
    }

}
