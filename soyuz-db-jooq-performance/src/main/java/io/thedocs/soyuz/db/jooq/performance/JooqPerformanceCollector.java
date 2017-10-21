package io.thedocs.soyuz.db.jooq.performance;

import com.google.common.collect.Ordering;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Возвращает статистику по длительности исполнения запросов к БД
 */
public class JooqPerformanceCollector {

    private static final Ordering<Item> SLOWEST = Ordering.natural().onResultOf(Item::getTotalDurationInMillis);

    private final Map<String, Item> itemsByQueries = new ConcurrentHashMap<>();

    public void add(String query, long durationInMillis) {
        Item item = itemsByQueries.get(query);

        if (item == null) {
            synchronized (this) {
                item = itemsByQueries.computeIfAbsent(query, k -> new Item(query));
            }
        }

        item.add(durationInMillis);
    }

    public void reset() {
        itemsByQueries.clear();
    }

    public Collection<Item> get() {
        return itemsByQueries.values();
    }

    public Collection<Item> getCopyAndReset() {
        Collection<Item> answer = new ArrayList<>(get());

        reset();

        return answer;
    }

    public List<Item> getSlowest() {
        return SLOWEST.sortedCopy(get());
    }

    @Getter
    public static class Item {
        private String query;
        private int invocationsCount;
        private long totalDurationInMillis;

        public Item(String query) {
            this.query = query;
            this.invocationsCount = 0;
            this.totalDurationInMillis = 0;
        }

        public void add(long durationInMillis) {
            this.invocationsCount++;
            this.totalDurationInMillis += durationInMillis;
        }

        @Override
        public String toString() {
            return "{" +
                    "d=" + totalDurationInMillis +
                    ", i=" + invocationsCount +
                    ", q='" + query + '\'' +
                    '}';
        }
    }

}
