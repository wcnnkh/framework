package run.soeasy.framework.core.collection;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;
import java.util.stream.Stream;

import lombok.NonNull;

public interface ReloadableElementsWrapper<S, W extends Elements<S>>
			extends Provider<S>, ElementsWrapper<S, W> {

		@Override
		default <U> Provider<U> convert(boolean resize, Function<? super Stream<S>, ? extends Stream<U>> converter) {
			return Provider.super.convert(resize, converter);
		}

		@Override
		default Provider<S> concat(Elements<? extends S> elements) {
			return Provider.super.concat(elements);
		}

		@Override
		default Stream<S> stream() {
			return ElementsWrapper.super.stream();
		}

		@Override
		default Provider<S> knownSize(@NonNull ToLongFunction<? super Elements<S>> statisticsSize) {
			return Provider.super.knownSize(statisticsSize);
		}

		@Override
		default Provider<S> filter(@NonNull Predicate<? super S> predicate) {
			return Provider.super.filter(predicate);
		}

		@Override
		default <U> Provider<U> map(Function<? super S, ? extends U> mapper) {
			return Provider.super.map(mapper);
		}
	}