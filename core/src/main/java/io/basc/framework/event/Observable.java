package io.basc.framework.event;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import io.basc.framework.event.AbstractObservable.ConvertObservable;
import io.basc.framework.event.AbstractObservable.FilterObservable;
import io.basc.framework.event.AbstractObservable.IfAbsentObservable;
import io.basc.framework.util.Optional;

public interface Observable<T> extends Optional<T>, EventRegistry<ObservableChangeEvent<T>> {

	public static <U> Observable<U> empty() {
		return new EmptyObservable<>();
	}

	@Override
	default <U> Observable<U> convert(Function<? super T, ? extends U> converter) {
		Objects.requireNonNull(converter);
		return new ConvertObservable<>(this, converter);
	}

	@Override
	default Observable<T> filter(Predicate<? super T> predicate) {
		Objects.requireNonNull(predicate);
		return new FilterObservable<T>(this, predicate);
	}

	@Override
	default Observable<T> ifAbsentGet(Supplier<? extends T> other) {
		Objects.requireNonNull(other);
		return new IfAbsentObservable<>(this, other);
	}

	@Override
	default Observable<T> ifAbsent(T other) {
		return ifAbsentGet(() -> other);
	}

	@Override
	default <U> Observable<U> map(Function<? super T, ? extends U> mapper) {
		return convert((e) -> e == null ? null : mapper.apply(e));
	}
}
