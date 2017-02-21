package io.belov.soyuz.tasks

import spock.lang.Specification

/**
 * Created by fbelov on 18.03.16.
 */
class TasksQueueToProcessSorterSpec extends Specification {

    private static final Random r = new Random()

    private TasksQueueToProcessSorterByPriority sorter = new TasksQueueToProcessSorterByPriority()

    private static final DATES = [
            d1: new Date(2015 - 1900, 12, 12),
            d2: new Date(2014 - 1900, 12, 30),
            d3: new Date(2013 - 1900, 01, 30),
            d4: new Date(2012 - 1900, 05, 26)
    ]

    private static final TASKS = [
            t1: createTask(90, DATES.d1, DATES.d1),
            t2: createTask(70, DATES.d3),
            t3: createTask(70, DATES.d2),
            t4: createTask(70, DATES.d4, DATES.d4),
            t5: createTask(70, DATES.d4, DATES.d3),
    ]

    def "should sort by priority / not queued yet / queued"() {
        when:
        def expected = [TASKS.t1, TASKS.t2, TASKS.t3, TASKS.t4, TASKS.t5]

        then:
        1000.times {
            def source = new ArrayList(TASKS.values())
            Collections.shuffle(source)

            assert sorter.sort(source) == expected
        }
    }

    private static createTask(int priority, Date postedOn, Date queuedOn = null) {
        return new Task(r.nextInt(100), priority, "ignore", postedOn, queuedOn, Task.Status.NEW, "ignore")
    }
}
