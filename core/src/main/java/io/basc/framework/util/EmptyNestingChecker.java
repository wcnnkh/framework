package io.basc.framework.util;

public class EmptyNestingChecker<E> implements NestingChecker<E> {
	public static final EmptyNestingChecker<Object> EMPTY = new EmptyNestingChecker<>();

	@Override
	public boolean isNestingExists(E element) {
		return false;
	}

	@Override
	public Registration registerNestedElement(E element) {
		return Registration.EMPTY;
	}

}
