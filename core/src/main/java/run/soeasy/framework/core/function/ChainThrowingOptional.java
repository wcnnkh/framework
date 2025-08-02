package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.NonNull;

class ChainThrowingOptional<S, V, E extends Throwable, T extends Throwable, W extends ThrowingSupplier<S, E>>
		extends ChainThrowingSupplier<S, V, E, T, W> implements ThrowingOptional<V, T> {

	public ChainThrowingOptional(@NonNull W source, @NonNull ThrowingFunction<? super S, ? extends V, T> mapper,
			ThrowingConsumer<? super S, ? extends E> endpoint, @NonNull Function<? super E, ? extends T> throwingMapper,
			boolean singleton) {
		super(source, mapper, endpoint, throwingMapper, singleton);
	}

	/**
	 * 对值进行扁平映射转换，返回映射后的结果。 若当前值为null，该方法不会执行映射函数，直接返回null。
	 *
	 * @param <R>    映射后的结果类型
	 * @param <X>    映射函数可能抛出的异常类型
	 * @param mapper 映射函数，不可为null
	 * @return 映射后的结果，若当前值为null则返回null
	 * @throws T 原始异常类型
	 * @throws X 映射函数抛出的异常
	 */
	@Override
	public <R, X extends Throwable> R flatMap(@NonNull ThrowingFunction<? super V, ? extends R, ? extends X> mapper)
			throws T, X {
		V target = super.get();
		return target != null ? mapper.apply(target) : null;
	}

	/**
	 * 添加额外的映射转换，返回新的MappingThrowingOptional实例。 新实例会先应用当前映射函数，再应用指定的映射函数。
	 *
	 * @param <R>    新的目标类型
	 * @param mapper 额外的映射函数，不可为null
	 * @return 新的MappingThrowingOptional实例
	 */
	@Override
	public <R> ChainThrowingOptional<S, R, E, T, W> map(@NonNull ThrowingFunction<? super V, ? extends R, T> mapper) {
		return new ChainThrowingOptional<>(this.source, this.mapper.andThen(mapper), endpoint, throwingMapper,
				singleton);
	}

	/**
	 * 返回当前实例，用于链式调用时保持接口一致性。
	 *
	 * @return 当前ThrowingOptional实例
	 */
	@Override
	public ThrowingOptional<V, T> optional() {
		return this;
	}
}