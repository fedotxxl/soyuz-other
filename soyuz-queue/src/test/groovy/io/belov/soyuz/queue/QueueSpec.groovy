package io.belov.soyuz.queue

import org.bson.types.ObjectId
import spock.lang.Specification

/**
 * Created by fbelov on 03.06.15.
 */
class QueueSpec extends Specification {

    def "should filter same actions"() {
        setup:
        def action = new QueueAction(_id: new ObjectId(), projectId: new ObjectId())
        def queue = new Queue("test", 10, new QueueSynchronizer())

        when:
        10.times {
            queue.push(new QueueAction(_id: action.id, projectId: action.projectId))
        }

        then:
        def actions = queue.getActions(action.projectId)

        assert actions.size() == 1
        assert actions[0] == action
    }

    def "should store max queue size actions"() {
        when:
        def maxQueueSizePerProject = 3
        def queue = new Queue("test", maxQueueSizePerProject, new QueueSynchronizer())
        def ids = (0..10).collect { new ObjectId() }
        def projects = [new ObjectId(), new ObjectId()]

        then:
        projects.each { projectId ->
            ids.eachWithIndex { id, i ->
                assert queue.push(new QueueAction(_id: id, projectId: projectId)) == (i < maxQueueSizePerProject)
            }
        }

        projects.each { projectId ->
            def actions = ids.subList(0, maxQueueSizePerProject).collect { id ->
                new QueueAction(_id: id, projectId: projectId)
            }

            assert TestUtils.compareSets(queue.getActions(projectId), actions)
        }
    }

    def "should return one action to process at the moment"() {
        setup:
        def projectId = new ObjectId()
        def queue = new Queue("test", 5, new QueueSynchronizer())
        def a1 = new QueueAction(_id: new ObjectId(), projectId: projectId)
        def a2 = new QueueAction(_id: new ObjectId(), projectId: projectId)

        when:
        queue.push(a1)
        queue.push(a2)

        then:
        assert queue.nextToProcess() == a1

        10.times {
            assert queue.nextToProcess() == null
        }
    }

    def "should return next action after previous action is processed"() {
        setup:
        def projectId = new ObjectId()
        def queue = new Queue("test", 5, new QueueSynchronizer())
        def a1 = new QueueAction(_id: new ObjectId(), projectId: projectId)
        def a2 = new QueueAction(_id: new ObjectId(), projectId: projectId)

        when:
        queue.push(a1)
        queue.push(a2)

        then:
        assert queue.nextToProcess() == a1
        assert queue.nextToProcess() == null

        queue.processed(a1.projectId)

        assert queue.nextToProcess() == a2
        assert queue.nextToProcess() == null
    }

    def "should return action per project"() {
        setup:
        def queue = new Queue("test", 3, new QueueSynchronizer())
        def (p1, p2) = getProjectIds()
        def actions = [:].withDefault { [] }

        [p1, p2].each { projectId ->
            5.times {
                actions[projectId] << new QueueAction(_id: new ObjectId(), projectId: projectId)
            }
        }

        when:
        actions[p1].each { queue.push(it) }
        actions[p2].each { queue.push(it) }

        then:
        assert queue.nextToProcess() == actions[p1][0]

        queue.processed(p1)

        assert queue.nextToProcess() == actions[p2][0]
        assert queue.nextToProcess() == actions[p1][1]
        assert queue.nextToProcess() == null
    }

    def "should sync project state between queues"() {
        setup:
        def sync = new QueueSynchronizer()
        def (p1, p2) = getProjectIds()
        def q1 = new Queue("test", 3, sync)
        def q2 = new Queue("test", 3, sync)
        def actions = [:].withDefault { [] }

        [p1, p2].each { projectId ->
            5.times {
                actions[projectId] << new QueueAction(_id: new ObjectId(), projectId: projectId)
            }
        }

        when:
        actions[p1].each {
            q1.push(it)
            q2.push(it)
        }
        actions[p2].each {
            q1.push(it)
            q2.push(it)
        }

        then:
        assert q1.nextToProcess() == actions[p1][0]
        assert q2.nextToProcess() == actions[p2][0]
        assert q1.nextToProcess() == null
        assert q2.nextToProcess() == null

        q1.processed(p1)
        q2.processed(p2)

        assert q2.nextToProcess() == actions[p1][0]

        q2.processed(p1)

        assert q2.nextToProcess() == actions[p2][1]
        assert q1.nextToProcess() == actions[p1][1]
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
