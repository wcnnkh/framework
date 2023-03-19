package io.basc.framework.util;

@FunctionalInterface
public interface Combiner<S, R, T> {
	T combine(S left, R right);
}