package com.project.poc_stream_gather.own_gather;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Gatherer;

public class MyGatherers {

    public static <T> Gatherer<T, Void, T> repeat(int times) {

        return Gatherer.ofSequential(

                () -> null,

                Gatherer.Integrator.ofGreedy((state, element, downstream) -> {

                    for (int i = 0; i < times; i++) {
                        downstream.push(element);
                    }

                    return true;
                })
        );
    }

    public static Gatherer<Integer, int[], Integer> runningSum() {

        return Gatherer.ofSequential(

                // Initializer
                () -> new int[]{0},

                // Integrator
                Gatherer.Integrator.ofGreedy((state, element, downstream) -> {

                    state[0] += element;

                    downstream.push(state[0]);

                    return true;
                })
        );
    }

    public static <T> Gatherer<T, Set<T>, T> distinctCustom() {

        return Gatherer.ofSequential(
                // Initializer
                HashSet::new,

                // Integrator
                Gatherer.Integrator.ofGreedy((seen, element, downstream) -> {

                    if (seen.add(element)) {
                        downstream.push(element);
                    }

                    return true;
                })
        );
    }

    public static <T> Gatherer<T, List<T>, List<T>> batch(int size) {

        return Gatherer.ofSequential(

                ArrayList::new,

                Gatherer.Integrator.ofGreedy((batch, element, downstream) -> {

                    batch.add(element);

                    if (batch.size() == size) {
                        downstream.push(List.copyOf(batch));
                        batch.clear();
                    }

                    return true;
                }),

                (batch, downstream) -> {
                    if (!batch.isEmpty()) {
                        downstream.push(List.copyOf(batch));
                    }
                }
        );
    }
}