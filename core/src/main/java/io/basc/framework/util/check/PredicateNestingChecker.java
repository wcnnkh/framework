package io.basc.framework.util.check;

import java.util.function.Predicate;

import io.basc.framework.util.Assert;
import io.basc.framework.util.exchange.Registration;

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
		return Registration.FAILURE;
	}

	public Predicate<? super E> getPredicate() {
		return predicate;
	}

}
