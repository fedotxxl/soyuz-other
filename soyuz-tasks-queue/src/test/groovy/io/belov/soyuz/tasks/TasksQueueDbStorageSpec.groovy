package io.belov.soyuz.tasks

import io.belov.soyuz.test.spring.AbstractIntegrationTransactionalSpec
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by fbelov on 03.03.16.
 */
class TasksQueueDbStorageSpec extends AbstractIntegrationTransactionalSpec {

    @Autowired
    private TasksQueueDao dao

    def "should insert tasks and return id"() {
        setup:
        def id

        when:
        id = dao.insert("md", 25, [movieId: 10])

        then:
        def task = dao.get(id)

        assert id == 1
        assert task.status == Task.Status.NEW
        assert task.type == "md"
        assert task.priority == 25
        assert task.context == '{"movieId":10}'

        when: "should increase id"
        id = dao.insert("md", 15, [movieId: 11])

        then:
        assert id == 2
    }
}
