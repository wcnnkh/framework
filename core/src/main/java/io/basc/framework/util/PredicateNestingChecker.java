package io.basc.framework.util;

import java.util.function.Predicate;

public class PredicateNestingChecker<E> implements NestingChecker<E> {
	private final Predicate<? super E> predicate;

	public PredicateNestingChecker(Predicate<? super E> predicate) {
		Assert.requiredArgument(predicate != null, "predicate");
		this.predicate = predicate;
	}

	@Override
	public boolean isNestingExists(E element) {
		return predicate.test(element);
	}

	@Override
	public Registration registerNestedElement(E element) {
		return Registration.EMPTY;
	}

	public Predicate<? super E> getPredicate() {
		return predicate;
	}

}