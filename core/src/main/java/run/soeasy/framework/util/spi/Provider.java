package run.soeasy.framework.util.spi;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.util.check.NestingChecker;
import run.soeasy.framework.util.collections.Elements;
import run.soeasy.framework.util.exchange.Registration;
import run.soeasy.framework.util.function.Consumer;
import run.soeasy.framework.util.function.Function;
import run.soeasy.framework.util.function.Optional;
import run.soeasy.framework.util.function.Predicate;

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
