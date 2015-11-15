package io.belov.soyuz.queue

import org.bson.types.ObjectId
import spock.lang.Specification

/**
 * Created by fbelov on 03.06.15.
 */
class QueueStackSpec extends Specification {

    def "should return actions from first queue first"() {
        setup:
        def sync = new QueueSynchronizer()
        def (p1, p2) = getProjectIds()
        def q1 = new Queue("test", 3)
        def q2 = new Queue("test", 3)
        def stack = new QueueStack(sync, new QueueWithHandlers(queue: q1), new QueueWithHandlers(queue: q2))
        def actions = [:].withDefault { [] }

        [p1, p2].each { projectId ->
            5.times {
                actions[projectId] << new QueueAction(_id: new ObjectId(), projectId: projectId)
            }
        }

        when:
        actions[p1].each {
            q2.push(it)
        }
        actions[p2].each {
            q2.push(it)
        }

        q1.push(actions[p1][0])
        q1.push(actions[p1][1])
        q1.push(actions[p2][0])

        then:
        assert stack.nextToProcess() == new ActionWithQueue(actions[p1][0], q1)
        assert stack.nextToProcess() == new ActionWithQueue(actions[p2][0], q1)

        q1.processed(p1)

        assert stack.nextToProcess() == new ActionWithQueue(actions[p1][1], q1)

        q1.processed(p2)

        assert stack.nextToProcess() == new ActionWithQueue(actions[p2][0], q2)
        assert stack.nextToProcess() == null
    }

    def getProjectIds() {
        def answer = []
        def set = new HashSet()

        set << new ObjectId()
        set << new ObjectId()

        for (def id : set) {
            answer << id
        }

        answer
    }
}
