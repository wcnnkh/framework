package io.basc.framework.core;


public interface Merge<O, T> {
	T merge(Iterable<O> origins);
}
