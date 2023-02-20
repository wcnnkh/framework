package io.basc.framework.context.transaction;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Return;
import io.basc.framework.util.Status;

public class DataResult<T> extends Result implements Return<T> {
	private static final long serialVersionUID = 1L;
	private final T data;

	public DataResult(boolean success, long code, String msg, boolean rollbackOnly, T data) {
		super(success, code, msg, rollbackOnly);
		this.data = data;
	}

	public DataResult(Status status, boolean rollbackOnly, T data) {
		super(status, rollbackOnly);
		this.data = data;
	}

	@Override
	public T get() {
		if (data == null) {
			String msg = getMsg();
			if (msg == null) {
				msg = NO_VALUE_PRESENT;
			}
			throw new NoSuchElementException(msg);
		}
		return data;
	}

	@Nullable
	public T getData() {
		return data;
	}

	@Override
	public boolean isPresent() {
		return data != null;
	}

	@Override
	public <U> DataResult<U> convert(Function<? super T, ? extends U> converter) {
		U u = converter.apply(data);
		return new DataResult<U>(this, isRollbackOnly(), u);
	}

	@Override
	public DataResult<T> ifAbsent(T other) {
		return convert((e) -> e == null ? other : e);
	}

	@Override
	public DataResult<T> ifAbsentGet(Supplier<? extends T> other) {
		return convert((e) -> e == null ? other.get() : e);
	}

	@Override
	public DataResult<T> filter(Predicate<? super T> predicate) {
		return convert((e) -> (e != null && predicate.test(e)) ? e : null);
	}

	@Override
	public <U> DataResult<U> map(Function<? super T, ? extends U> mapper) {
		Objects.requireNonNull(mapper);
		return convert((e) -> e == null ? null : mapper.apply(e));
	}

	public static <U> DataResult<U> success(U value) {
		return success(0, null, value);
	}

	public static <U> DataResult<U> success(long code, @Nullable String msg, @Nullable U value) {
		return new DataResult<U>(true, code, msg, false, value);
	}

	public static <U> DataResult<U> error(String msg, @Nullable U value) {
		return error(0, msg, value);
	}

	public static <U> DataResult<U> error(long code, @Nullable String msg, @Nullable U value) {
		return new DataResult<U>(false, code, msg, true, value);
	}
}
