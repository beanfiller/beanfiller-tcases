package io.github.beanfiller.annotation.sample;

import java.util.Collection;
import java.util.Iterator;

public class CircleIterator<T> {
    private Iterator<T> iterator;
    private boolean exhausted = false;
    private final Collection<T> wrapped;

    public CircleIterator(final Collection<T> wrapped) {
        this.wrapped = wrapped;
        iterator = wrapped.iterator();
        if (!iterator.hasNext()) {
            throw new IllegalStateException("Iterator exhausted after creation");
        }
    }

    public T next() {
        if (!iterator.hasNext()) {
            iterator = wrapped.iterator();
            if (!iterator.hasNext()) {
                throw new IllegalStateException("Iterator exhausted after creation");
            }
        }
        T result = iterator.next();
        if (!iterator.hasNext()) {
            exhausted = true;
        }
        return result;
    }

    public boolean isExhausted() {
        return exhausted;
    }
}
