package run.soeasy.framework.core.collection;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;
import java.util.stream.Stream;

import lombok.NonNull;

@FunctionalInterface
public interface ProviderWrapper<S, W extends Provider<S>> extends Provider<S>, ElementsWrapper<S, W> {
	@Override
	default void reload() {
		getSource().reload();
	}

	@Override
	default Stream<S> stream() {
		return getSource().stream();
	}

	@Override
	default <U> Provider<U> map(boolean resize, Function<? super Stream<S>, ? extends Stream<U>> converter) {
		return getSource().map(resize, converter);
	}

	@Override
	default Provider<S> concat(Elements<? extends S> elements) {
		return getSource().concat(elements);
	}

	@Override
	default Provider<S> concat(Provider<? extends S> serviceLoader) {
		return getSource().concat(serviceLoader);
	}

	@Override
	default Provider<S> knownSize(@NonNull ToLongFunction<? super Elements<S>> statisticsSize) {
		return getSource().knownSize(statisticsSize);
	}

	@Override
	default Provider<S> filter(@NonNull Predicate<? super S> predicate) {
		return getSource().filter(predicate);
	}

	@Override
	default <U> Provider<U> map(Function<? super S, ? extends U> mapper) {
		return getSource().map(mapper);
	}
}