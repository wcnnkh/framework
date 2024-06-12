package io.basc.framework.util;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.function.Optional;

public interface Return<T> extends Optional<T>, Status {
	static <U> Return<U> success() {
		return success(null);
	}

	static <U> Return<U> success(U value) {
		return success(0, null, value);
	}

	static <U> Return<U> success(long code, @Nullable String msg) {
		return success(code, msg, null);
	}

	static <U> Return<U> success(long code, @Nullable String msg, @Nullable U value) {
		return new StandardReturn<U>(true, code, msg, value);
	}

	static <U> Return<U> error(@Nullable String msg) {
		return error(0, msg);
	}

	static <U> Return<U> error(String msg, @Nullable U value) {
		return error(0, msg, value);
	}

	static <U> Return<U> error(long code, @Nullable String msg) {
		return error(code, msg, null);
	}

	static <U> Return<U> error(long code, @Nullable String msg, @Nullable U value) {
		return new StandardReturn<U>(false, code, msg, value);
	}

	<U> Return<U> convert(Function<? super T, ? extends U> converter);

	@Override
	default Return<T> ifAbsent(T other) {
		return convert((e) -> e == null ? other : e);
	}

	@Override
	default Return<T> ifAbsentGet(Supplier<? extends T> other) {
		return convert((e) -> e == null ? other.get() : e);
	}

	@Override
	default Return<T> filter(Predicate<? super T> predicate) {
		return convert((e) -> (e != null && predicate.test(e)) ? e : null);
	}

	@Override
	default <U> Return<U> map(Function<? super T, ? extends U> mapper) {
		Objects.requireNonNull(mapper);
		return convert((e) -> e == null ? null : mapper.apply(e));
	}
}
