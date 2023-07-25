package io.basc.framework.util.function;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import io.basc.framework.lang.Nullable;

/**
 * 一般来说只用重写以下方法{@see #ifPresent(ConsumeProcessor)},
 * {@see #flatConvert(Function))}
 * 
 * @author wcnnkh
 *
 * @param <T>
 */
@FunctionalInterface
public interface Optional<T> extends Supplier<T> {

	@SuppressWarnings("unchecked")
	public static <U> Optional<U> empty() {
		return (Optional<U>) DefaultOptional.EMPTY;
	}

	public static <U> Optional<U> of(@Nullable U value) {
		if (value == null) {
			return empty();
		}
		return new DefaultOptional<>(value);
	}

	public static <U> Optional<U> ofSupplier(Supplier<? extends U> valueSupplier) {
		return new StandardOptional<>(valueSupplier);
	}

	/**
	 * 调用{@link #flatConvert(Function)}
	 * 
	 * @param <U>
	 * @param converter
	 * @return
	 */
	default <U> Optional<U> convert(Function<? super T, ? extends U> converter) {
		Objects.requireNonNull(converter);
		return flatConvert((e) -> {
			U value = converter.apply(e);
			return of(value);
		});
	}

	/**
	 * 调用{@link #map(Function)}
	 * 
	 * @param predicate
	 * @return
	 */
	default Optional<T> filter(Predicate<? super T> predicate) {
		Objects.requireNonNull(predicate);
		return map((value) -> predicate.test(value) ? null : value);
	}

	/**
	 * 一般来说这是{@link Optional}类特有的方法,目的是为了将调用方式聚合
	 * <p>
	 * 
	 * 这和{@link #convert(Function)}的区别是converter的元素可能为空
	 * 
	 * @param <U>
	 * @param <E>
	 * @param converter
	 * @return
	 * @throws E
	 */
	@Nullable
	default <U, E extends Throwable> U flatConvert(Processor<? super T, ? extends U, ? extends E> converter) throws E {
		Objects.requireNonNull(converter);
		T value = orElse(null);
		return converter.process(value);
	}

	default <U> Optional<U> flatMap(Function<? super T, ? extends Optional<U>> mapper) {
		Objects.requireNonNull(mapper);
		return flatConvert((e) -> e == null ? empty() : Objects.requireNonNull(mapper.apply(e)));
	}

	default T get() throws NoSuchElementException {
		T value = orElse(null);
		if (value == null) {
			throw new NoSuchElementException("No value present");
		}
		return value;
	}

	/**
	 * 调用{@link #ifAbsentGet(Supplier)}
	 * 
	 * @param other
	 * @return
	 */
	default Optional<T> ifAbsent(T other) {
		return ifAbsentGet(() -> other);
	}

	/**
	 * 调用{@link #convert(Function)}
	 * 
	 * @param other
	 * @return
	 */
	default Optional<T> ifAbsentGet(Supplier<? extends T> other) {
		Objects.requireNonNull(other);
		return convert((e) -> e == null ? other.get() : e);
	}

	default <E extends Throwable> void ifPresent(ConsumeProcessor<? super T, ? extends E> consumer) throws E {
		T value = orElse(null);
		if (value != null) {
			consumer.process(value);
		}
	}

	default boolean isPresent() {
		return orElse(null) != null;
	}

	/**
	 * 调用{@link #convert(Function)}
	 * 
	 * @param <U>
	 * @param mapper
	 * @return
	 */
	default <U> Optional<U> map(Function<? super T, ? extends U> mapper) {
		Objects.requireNonNull(mapper);
		return convert((e) -> e == null ? null : mapper.apply(e));
	}

	/**
	 * 为了保证一些实现的原子性，默认不实现此方法
	 * 
	 * @param other
	 * @return
	 */
	T orElse(T other);

	default <E extends Throwable> T orElseGet(Source<? extends T, ? extends E> other) throws E {
		T value = orElse(null);
		return value == null ? other.get() : value;
	}

	default <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
		T value = orElse(null);
		if (value == null) {
			throw exceptionSupplier.get();
		}
		return value;
	}
}
