package io.basc.framework.event;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import io.basc.framework.util.Optional;

public interface Observable<T> extends Optional<T>, BroadcastEventRegistry<ChangeEvent<T>> {

	@SuppressWarnings("unchecked")
	public static <U> Observable<U> empty() {
		return (Observable<U>) EmptyObservable.INSTANCE;
	}

	@Override
	default <U> Observable<U> convert(Function<? super T, ? extends U> converter) {
		Objects.requireNonNull(converter);
		return new ConvertObservable<>(this, converter);
	}

	@Override
	default Observable<T> filter(Predicate<? super T> predicate) {
		Objects.requireNonNull(predicate);
		return convert((e) -> (e == null || predicate.test(e)) ? e : null);
	}

	@Override
	default Observable<T> ifAbsentGet(Supplier<? extends T> other) {
		Objects.requireNonNull(other);
		return convert((e) -> e == null ? other.get() : e);
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
