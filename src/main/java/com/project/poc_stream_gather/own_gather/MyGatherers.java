package com.project.poc_stream_gather.own_gather;

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
}