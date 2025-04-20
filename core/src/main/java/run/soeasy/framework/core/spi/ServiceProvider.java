package run.soeasy.framework.core.spi;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import run.soeasy.framework.core.exe.Function;
import run.soeasy.framework.core.exe.Optional;
import run.soeasy.framework.core.exe.Predicate;

@Getter
@Setter
public class ServiceProvider<T, E extends Throwable> extends ConfigurableServices<T> {
	@NonNull
	private ThreadLocal<Set<T>> nestingChecker = new ThreadLocal<Set<T>>();

	public Optional<T, E> optional() {
		return optional(Predicate.alwaysTruePredicate());
	}

	public Optional<T, E> optional(Predicate<? super T, ? extends E> predicate) {
		return new SingleProvider(predicate);
	}

	@RequiredArgsConstructor
	private class SingleProvider implements Optional<T, E> {
		@NonNull
		private final Predicate<? super T, ? extends E> predicate;

		@Override
		public <R, X extends Throwable> R apply(@NonNull Function<? super T, ? extends R, ? extends X> mapper)
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
