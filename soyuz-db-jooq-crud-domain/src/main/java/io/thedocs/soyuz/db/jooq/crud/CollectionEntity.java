package io.thedocs.soyuz.db.jooq.crud;

import io.thedocs.soyuz.to;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * Хранит набор item'ов с общим количеством доступных
 */
public class CollectionEntity<T> {
    private List<T> items;
    private int total;

    public CollectionEntity(List<T> items) {
        this(items, (items != null) ? items.size() : 0);
    }

    public CollectionEntity(List<T> items, int total) {
        this.items = items;
        this.total = total;
    }

    public List<T> getItems() {
        return items;
    }

    public int getTotal() {
        return total;
    }

    public <R> CollectionEntity<R> transform(Function<T, R> transformer) {
        return new CollectionEntity<R>(to.list(items, transformer), total);
    }

    public <R> CollectionEntity<R> transformList(Function<Collection<T>, List<R>> transformer) {
        return new CollectionEntity<R>(transformer.apply(items), total);
    }
}
