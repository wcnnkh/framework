package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.NonNull;

/**
 * ThrowingOptional接口的链式实现类，继承自{@link ChainThrowingSupplier}，
 * 用于处理可能为null的值及关联的异常，支持值的链式映射与扁平映射操作，同时集成资源关闭和异常转换逻辑。
 * 适用于需要安全处理可能为null的结果，且需管理异常和资源生命周期的场景。
 *
 * @param <S> 源值类型（原始供应者提供的基础值类型）
 * @param <V> 目标值类型（经过映射转换后的值类型，可能为null）
 * @param <E> 源异常类型（原始供应者可能抛出的异常类型）
 * @param <T> 目标异常类型（经过转换后的异常类型）
 * @param <W> 源供应者类型（提供源值的{@link ThrowingSupplier}实现类）
 * @see ThrowingOptional
 * @see ChainThrowingSupplier
 */
class ChainThrowingOptional<S, V, E extends Throwable, T extends Throwable, W extends ThrowingSupplier<S, E>>
		extends ChainThrowingSupplier<S, V, E, T, W> implements ThrowingOptional<V, T> {

	/**
	 * 构造链式ThrowingOptional实例，初始化源供应者、关闭操作、映射函数及异常转换逻辑。
	 *
	 * @param source         源值供应者，提供原始值的获取逻辑，不可为null
	 * @param closer         值获取后执行的关闭操作，用于资源清理，不可为null
	 * @param mapper         源值到目标值的映射转换函数，不可为null
	 * @param throwingMapper 异常转换函数，将源异常（E类型）转换为目标异常（T类型），不可为null
	 */
	public ChainThrowingOptional(W source, ThrowingRunnable<? extends T> closer,
			ThrowingFunction<? super S, ? extends V, T> mapper, Function<? super E, ? extends T> throwingMapper) {
		super(source, closer, mapper, throwingMapper);
	}

	/**
	 * 对当前值执行扁平映射转换，直接返回映射函数的结果（可能为null）。
	 * 若当前值为null，映射函数不会被执行，直接返回null；否则将当前值传入映射函数并返回其结果。
	 *
	 * @param <R>    扁平映射后的结果类型
	 * @param <X>    映射函数可能抛出的异常类型
	 * @param mapper 扁平映射函数，接收当前目标值（V类型）并返回新结果（R类型），不可为null
	 * @return 映射后的结果（R类型），若当前值为null则返回null
	 * @throws T 原始操作或异常转换可能抛出的目标异常
	 * @throws X 映射函数执行过程中可能抛出的异常
	 */
	@Override
	public <R, X extends Throwable> R flatMap(@NonNull ThrowingFunction<? super V, ? extends R, ? extends X> mapper)
			throws T, X {
		V target = super.get();
		return target != null ? mapper.apply(target) : null;
	}

	/**
	 * 添加额外的映射转换步骤，返回包含组合转换逻辑的新ThrowingOptional实例。
	 * 新实例会先执行当前实例的映射函数，再应用新的映射函数，保持原有的源供应者、关闭操作和异常转换逻辑。
	 *
	 * @param <R>    新的目标值类型（新增映射后的类型）
	 * @param mapper 新增的映射函数，接收当前目标值（V类型）并返回新值（R类型），不可为null
	 * @return 新的{@link ChainThrowingOptional}实例，包含组合后的映射逻辑
	 */
	@Override
	public <R> ThrowingOptional<R, T> map(@NonNull ThrowingFunction<? super V, ? extends R, T> mapper) {
		return new ChainThrowingOptional<>(this.source, this.closeable, this.mapper.andThen(mapper),
				this.throwingMapper);
	}

	/**
	 * 返回当前ThrowingOptional实例，用于链式调用时保持接口一致性，明确标识当前操作返回Optional类型。
	 *
	 * @return 当前{@link ChainThrowingOptional}实例本身
	 */
	@Override
	public ThrowingOptional<V, T> optional() {
		return this;
	}
}