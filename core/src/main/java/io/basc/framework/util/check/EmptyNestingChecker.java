package io.basc.framework.util.check;

import io.basc.framework.util.register.Registration;

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

	@Override
	public NestingChecker<E> or(NestingChecker<E> right) {
		return right == null ? this : right;
	}
}
