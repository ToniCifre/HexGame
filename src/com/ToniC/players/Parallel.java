package com.ToniC.players;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Parallel {
    private static final int NUM_CORES = Runtime.getRuntime().availableProcessors();

    private static final ExecutorService forPool = Executors.newFixedThreadPool(NUM_CORES * 2);

    public static <T> void For(final Iterable<T> elements, final Operation<T> operation) {
        try {
            forPool.invokeAll(createCallables(elements, operation));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static <T> Collection<Callable<Void>> createCallables(final Iterable<T> elements, final Operation<T> operation) {
        List<Callable<Void>> callables = new LinkedList<>();
        for (T elem : elements) {
            callables.add(() -> {
                operation.perform(elem);
                return null;
            });
        }

        return callables;
    }

    public static interface Operation<T> {
        public void perform(T pParameter);
    }

}
