package io.basc.framework.util.spi;

import io.basc.framework.util.check.NestingChecker;
import io.basc.framework.util.check.ThreadLocalNestingChecker;
import io.basc.framework.util.function.Predicate;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class Providers<T, E extends Throwable> extends ConfigurableServices<T> {
	@NonNull
	private NestingChecker<? super T> nestingChecker = new ThreadLocalNestingChecker<>();

	public Provider<T, E> optional() {
		return optional(Predicate.alwaysTruePredicate());
	}

	public Provider<T, E> optional(Predicate<? super T, ? extends E> predicate) {
		return new Provider<>(this, nestingChecker, predicate);
	}
}
