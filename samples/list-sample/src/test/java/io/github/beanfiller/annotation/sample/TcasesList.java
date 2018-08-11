package io.github.beanfiller.annotation.sample;

import java.util.Collection;

public abstract class TcasesList<T> {

    public abstract Class<T> getElementClass();

    public abstract Collection<T> createFillerElements(int i);
}
