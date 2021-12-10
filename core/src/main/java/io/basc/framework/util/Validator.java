package io.basc.framework.util;

@FunctionalInterface
public interface Validator<D, E> {
	boolean verify(D source, E encode);
}
