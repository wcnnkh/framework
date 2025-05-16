package run.soeasy.framework.core.function.lang;

import java.util.function.Function;
import java.util.function.Predicate;

public interface RuntimeThrowingPredicate<S, E extends RuntimeException> extends ThrowingPredicate<S, E>, Predicate<S> {

	@Override
	default RuntimeThrowingPredicate<S, E> negate() {
		return new RuntimePredicate<>(this.negate(), Function.identity());
	}
}
