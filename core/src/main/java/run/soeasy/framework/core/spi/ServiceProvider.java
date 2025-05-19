package run.soeasy.framework.core.spi;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import run.soeasy.framework.core.function.ThrowingFunction;
import run.soeasy.framework.core.function.ThrowingOptional;
import run.soeasy.framework.core.function.lang.ThrowingPredicate;

@Getter
@Setter
public class ServiceProvider<T, E extends Throwable> extends ConfigurableServices<T> {
	@NonNull
	private ThreadLocal<Set<T>> nestingChecker = new ThreadLocal<Set<T>>();

	public ThrowingOptional<T, E> optional() {
		return optional(ThrowingPredicate.alwaysTrue());
	}

	public ThrowingOptional<T, E> optional(ThrowingPredicate<? super T, ? extends E> predicate) {
		return new SingleProvider(predicate);
	}

	@RequiredArgsConstructor
	private class SingleProvider implements ThrowingOptional<T, E> {
		@NonNull
		private final ThrowingPredicate<? super T, ? extends E> predicate;

		@Override
		public <R, X extends Throwable> R flatMap(@NonNull ThrowingFunction<? super T, ? extends R, ? extends X> mapper)
				throws E, X {
			Set<T> nestingSet = nestingChecker.get();
			if (nestingSet == null) {
				nestingSet = new HashSet<>();
				nestingChecker.set(nestingSet);
			}

			try {
				for (T element : ServiceProvider.this) {
					if (!predicate.test(element)) {
						continue;
					}

					if (nestingSet.add(element)) {
						try {
							R value = mapper.apply(element);
							if (value == null) {
								continue;
							}

							return value;
						} finally {
							nestingSet.remove(element);
						}
					}
				}
			} finally {
				nestingChecker.remove();
			}
			return null;
		}
	}
}
