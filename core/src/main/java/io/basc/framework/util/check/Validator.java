package io.basc.framework.util.check;

@FunctionalInterface
public interface Validator<D, E> {
	boolean verify(D source, E encode);
}
