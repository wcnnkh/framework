package run.soeasy.framework.core.function;

import java.util.NoSuchElementException;
import java.util.function.Function;

import lombok.NonNull;

public interface ThrowingOptional<T, E extends Throwable> extends ThrowingSupplier<T, E> {

	/**
	 * 从ThrowingSupplier创建ThrowingOptional实例。
	 * 如果Supplier本身已经是ThrowingOptional，则直接返回；否则调用其optional()方法转换。
	 *
	 * @param <U>      值的类型
	 * @param <E>      异常类型
	 * @param supplier 供应者，不可为null
	 * @return ThrowingOptional实例
	 */
	public static <U, E extends Throwable> ThrowingOptional<U, E> forSupplier(
			@NonNull ThrowingSupplier<U, E> supplier) {
		if (supplier instanceof ThrowingOptional) {
			return (ThrowingOptional<U, E>) supplier;
		}
		return supplier.optional();
	}

	/**
	 * 从值创建ThrowingOptional实例。 如果值为null，则返回空的ThrowingOptional；否则返回包含该值的实例。
	 *
	 * @param <U>   值的类型
	 * @param <E>   异常类型
	 * @param value 值，可以为null
	 * @return 包含值的ThrowingOptional或空的ThrowingOptional
	 */
	public static <U, E extends Throwable> ThrowingOptional<U, E> forValue(U value) {
		if (value == null) {
			return empty();
		}
		return new ValueThrowingOptional<>(value);
	}

	/**
	 * 获取空的ThrowingOptional实例。 空实例表示值不存在，调用get()方法会抛出NoSuchElementException。
	 *
	 * @param <U> 值的类型
	 * @param <E> 异常类型
	 * @return 空的ThrowingOptional实例
	 */
	@SuppressWarnings("unchecked")
	public static <U, E extends Throwable> ThrowingOptional<U, E> empty() {
		return (ThrowingOptional<U, E>) ValueThrowingOptional.EMPTY;
	}

	/**
	 * 过滤值，根据谓词条件决定是否保留值。 如果谓词测试返回true，则保留值；否则将值置为null（转换为空的Optional）。
	 *
	 * @param filter 过滤谓词，不可为null
	 * @return 过滤后的ThrowingOptional实例
	 */
	default ThrowingOptional<T, E> filter(@NonNull ThrowingPredicate<? super T, ? extends E> filter) {
		return map((e) -> filter.test(e) ? e : null);
	}

	/**
	 * 获取值，如果值不存在则抛出NoSuchElementException。 该方法会先调用orElse(null)获取值，若值为null则抛出异常。
	 *
	 * @return 存在的值
	 * @throws E                      可能抛出的异常
	 * @throws NoSuchElementException 值不存在时抛出
	 */
	@Override
	default T get() throws E, NoSuchElementException {
		T value = orElse(null);
		if (value == null) {
			throw new NoSuchElementException("No value present");
		}
		return value;
	}

	/**
	 * 对值进行扁平映射转换，返回映射后的结果。 该方法允许将当前Optional中的值转换为另一种类型，可能抛出新的异常类型。
	 *
	 * @param <R>    映射后的结果类型
	 * @param <X>    可能抛出的新异常类型
	 * @param mapper 映射函数，不可为null
	 * @return 映射后的结果
	 * @throws E 原始异常类型
	 * @throws X 新的异常类型
	 */
	<R, X extends Throwable> R flatMap(@NonNull ThrowingFunction<? super T, ? extends R, ? extends X> mapper)
			throws E, X;

	/**
	 * 如果值存在，则执行指定的消费操作。 该方法会先检查值是否存在，存在则执行consumer，否则不执行任何操作。
	 *
	 * @param <X>      消费操作可能抛出的异常类型
	 * @param consumer 消费函数，不可为null
	 * @throws E 原始异常类型
	 * @throws X 消费操作可能抛出的异常
	 */
	default <X extends Throwable> void ifPresent(ThrowingConsumer<? super T, ? extends X> consumer) throws E, X {
		flatMap((e) -> {
			if (e != null) {
				consumer.accept(e);
			}
			return e;
		});
	}

	/**
	 * 检查值是否存在。 通过调用orElse(null)判断返回值是否为null来确定值是否存在。
	 *
	 * @return 值存在返回true，否则返回false
	 * @throws E 可能抛出的异常
	 */
	default boolean isPresent() throws E {
		return orElse(null) != null;
	}

	/**
	 * 对值进行映射转换，返回新的ThrowingOptional。 该方法支持函数式编程的map操作，将T类型的值转换为R类型的值。
	 *
	 * @param <R>    映射后的结果类型
	 * @param mapper 映射函数，不可为null
	 * @return 映射后的ThrowingOptional实例
	 */
	@Override
	default <R> ThrowingOptional<R, E> map(@NonNull ThrowingFunction<? super T, ? extends R, E> mapper) {
		return new ChainThrowingOptional<>(this, ThrowingRunnable.ignore(), mapper, Function.identity());
	}

	/**
	 * 获取值，如果值不存在则返回指定的默认值。 该方法通过flatMap实现，如果值存在则返回值，否则返回other。
	 *
	 * @param other 默认值
	 * @return 值或默认值
	 * @throws E 可能抛出的异常
	 */
	default T orElse(T other) throws E {
		return flatMap((e) -> e != null ? e : other);
	}

	/**
	 * 获取值，如果值不存在则通过供应者获取默认值。 该方法在值不存在时调用供应者，避免不必要的默认值创建。
	 *
	 * @param <X>     供应者可能抛出的异常类型
	 * @param suppler 供应者，不可为null
	 * @return 值或供应者提供的默认值
	 * @throws E 原始异常类型
	 * @throws X 供应者可能抛出的异常
	 */
	default <X extends Throwable> T orElseGet(@NonNull ThrowingSupplier<? extends T, ? extends X> suppler) throws E, X {
		return flatMap((e) -> e != null ? e : suppler.get());
	}

	/**
	 * 获取值，如果值不存在则抛出指定的异常。 该方法在值不存在时调用异常供应者创建并抛出异常。
	 *
	 * @param <X>               要抛出的异常类型
	 * @param exceptionSupplier 异常供应者，不可为null
	 * @return 存在的值
	 * @throws E 原始异常类型
	 * @throws X 值不存在时抛出的异常
	 */
	default <X extends Throwable> T orElseThrow(ThrowingSupplier<? extends X, ? extends X> exceptionSupplier)
			throws E, X {
		return flatMap((e) -> {
			if (e == null) {
				throw exceptionSupplier.get();
			}
			return e;
		});
	}

	/**
	 * 返回自身，用于链式调用时保持接口一致性。 由于当前对象已经是ThrowingOptional，直接返回自身。
	 *
	 * @return 当前ThrowingOptional实例
	 */
	@Override
	default ThrowingOptional<T, E> optional() {
		return this;
	}
}