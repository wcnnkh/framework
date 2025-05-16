package run.soeasy.framework.core.function.lang;

public interface RuntimeThrowingPredicateWrapper<S, E extends RuntimeException, W extends RuntimeThrowingPredicate<S, E>>
		extends RuntimeThrowingPredicate<S, E>, ThrowingPredicateWrapper<S, E, W> {

	@Override
	default RuntimeThrowingPredicate<S, E> negate() {
		return getSource().negate();
	}
}
