package io.thedocs.soyuz.err

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.EqualsAndHashCode
import spock.lang.Specification
/**
 * Created by fbelov on 08.10.16.
 */
class AnswerOrErrorsSpec extends Specification {

    def "should serialize / deserialize to / from json"() {
        when:
        def om = new ObjectMapper()

        then:
        assert om.writeValueAsString(answer) == json
        assert om.readValue(json, AnswerOrErrors.Deserializer).toAnswerOrErrors() == answer

        where:
        answer << [
                AnswerOrErrors.failure(Err.field("thumbnails").code("defaultNotSet").value(["id":"600,0104c66f88e83774", "server":"dev.search.devadmin.com:8073"]).build()),
                AnswerOrErrors.ok(),
        ]
        json << [
               '{"answer":null,"ok":false,"errors":[{"field":"thumbnails","code":"defaultNotSet","message":null,"value":{"id":"600,0104c66f88e83774","server":"dev.search.devadmin.com:8073"},"args":null}]}',
                '{"answer":null,"ok":true,"errors":null}'
        ]
    }

    def "should serialize / deserialize generic types"() {
        when:
        def om = new ObjectMapper()

        then:
        assert om.writeValueAsString(answer) == json
        assert om.readValue(json, new TypeReference<AnswerOrErrors.Deserializer<Car>>() {}).toAnswerOrErrors() == answer

        where:
        answer << [
                AnswerOrErrors.ok(new Car(label: "Lada"))
        ]
        json << [
                '{"answer":{"label":"Lada"},"ok":true,"errors":null}'
        ]
    }

    @EqualsAndHashCode
    public static class Car {
        String label
    }

    private getGenericType(Class clazz) {

    }
}
