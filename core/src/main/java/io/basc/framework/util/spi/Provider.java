package io.basc.framework.util.spi;

import io.basc.framework.util.check.NestingChecker;
import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.exchange.Registration;
import io.basc.framework.util.function.Consumer;
import io.basc.framework.util.function.Function;
import io.basc.framework.util.function.Optional;
import io.basc.framework.util.function.Predicate;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Provider<T, E extends Throwable> implements Optional<T, E> {
	@NonNull
	private final Elements<? extends T> elements;
	@NonNull
	private final NestingChecker<? super T> nestingChecker;
	@NonNull
	private final Predicate<? super T, ? extends E> predicate;

	@Override
	public <R, X extends Throwable> R apply(@NonNull Function<? super T, ? extends R, ? extends X> mapper) throws E, X {
		for (T element : elements) {
			if (!predicate.test(element)) {
				continue;
			}

			if (nestingChecker.isNestingExists(element)) {
				continue;
			}

			Registration registration = nestingChecker.registerNestedElement(element);
			try {
				R value = mapper.apply(element);
				if (value == null) {
					continue;
				}

				return value;
			} finally {
				registration.cancel();
			}
		}
		return null;
	}

	public <X extends Throwable> void accept(Consumer<? super T, ? extends X> consumer) throws E, X {
		apply((e) -> {
			consumer.accept(e);
			return null;
		});
	}
}
