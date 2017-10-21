package io.thedocs.soyuz.func.processor;

import com.google.common.collect.Lists;
import io.thedocs.soyuz.is;
import io.thedocs.soyuz.to;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created on 11.07.17.
 */
@AllArgsConstructor
public class PartitionProcessor<T> {
    private int partitionSize;

    public void process(Collection<T> items, Consumer<List<T>> function) {
        if (is.t(items)) {
            for (List<T> subList : partition(items)) {
                function.accept(subList);
            }
        }
    }

    private List<List<T>> partition(Collection<T> items) {
        List<T> itemsAsList = to.list(items);

        if (partitionSize > 0) {
            return Lists.partition(itemsAsList, partitionSize);
        } else {
            List<List<T>> answer = new ArrayList<>();

            answer.add(itemsAsList);

            return answer;
        }
    }
}
