package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 链式可抛出异常的供应者实现类，实现{@link ThrowingSupplier}接口，支持值的获取、转换、关闭操作及异常转换的链式组合。
 * 整合了源值供应、映射转换、后置关闭操作和异常类型转换逻辑，适用于需要在值获取后执行清理操作或处理异常的场景。
 *
 * @param <S> 源值类型（原始供应者提供的基础值类型）
 * @param <V> 目标值类型（经过映射转换后的值类型）
 * @param <E> 源异常类型（原始供应者可能抛出的异常类型）
 * @param <T> 目标异常类型（经过转换后的异常类型）
 * @param <W> 源供应者类型（提供源值的{@link ThrowingSupplier}实现类）
 * @see ThrowingSupplier
 */
@RequiredArgsConstructor
class ChainThrowingSupplier<S, V, E extends Throwable, T extends Throwable, W extends ThrowingSupplier<S, E>>
		implements ThrowingSupplier<V, T> {
	/**
	 * 源值供应者，提供原始值的获取逻辑，是整个链式操作的数据源，不可为null
	 */
	protected final W source;

	/**
	 * 值获取完成后执行的关闭操作，通常用于资源清理（如释放连接、关闭流等），不可为null
	 */
	protected final ThrowingRunnable<? extends T> closeable;

	/**
	 * 源值到目标值的映射转换函数，负责将S类型的源值转换为V类型的目标值，不可为null
	 */
	protected final ThrowingFunction<? super S, ? extends V, T> mapper;

	/**
	 * 异常转换函数，将源异常（E类型）转换为目标异常（T类型），实现异常类型的统一处理，不可为null
	 */
	protected final Function<? super E, ? extends T> throwingMapper;

	/**
	 * 获取经过转换的目标值，执行流程包括：获取源值 → 处理源异常 → 执行关闭操作 → 应用映射转换。
	 * 无论源值获取是否成功，都会执行{@link #closeable}定义的关闭操作，确保资源清理。
	 *
	 * @return 映射转换后的目标值（V类型）
	 * @throws T 可能抛出的目标异常（经{@code throwingMapper}转换后）
	 */
	@Override
	public V get() throws T {
		return flatMap(ThrowingFunction.identity());
	}

	@SuppressWarnings("unchecked")
	public <R, X extends Throwable> R flatMap(@NonNull ThrowingFunction<? super V, ? extends R, ? extends X> mapper)
			throws T, X {
		S value;
		try {
			try {
				value = this.source.get();
			} catch (Throwable e) {
				throw throwingMapper.apply((E) e);
			}
			V source = this.mapper.apply(value);
			return source == null ? null : mapper.apply(source);
		} finally {
			closeable.run();
		}
	}

	/**
	 * 添加新的映射转换步骤，返回包含组合转换逻辑的新链式供应者。
	 * 新实例会先执行当前实例的映射，再应用新的映射函数，形成链式转换，保持原有的源供应者、关闭操作和异常转换逻辑。
	 *
	 * @param <R>    新的目标值类型（新增映射后的类型）
	 * @param mapper 新增的映射函数，接收当前目标值（V类型）并返回新值（R类型），不可为null
	 * @return 新的{@link ChainThrowingSupplier}实例，包含组合后的映射逻辑
	 */
	@Override
	public <R> ThrowingSupplier<R, T> map(@NonNull ThrowingFunction<? super V, ? extends R, T> mapper) {
		return new ChainThrowingSupplier<>(this.source, this.closeable, this.mapper.andThen(mapper), throwingMapper);
	}

	/**
	 * 创建支持流水线操作的{@link Pipeline}实例，并绑定当前的关闭操作。
	 * 流水线可进一步扩展资源管理逻辑，如注册额外关闭回调，确保资源在流水线生命周期结束后正确释放。
	 *
	 * @return 绑定当前关闭操作的{@link Pipeline}实例
	 */
	@Override
	public Pipeline<V, T> closeable() {
		Pipeline<V, T> pipline = new ChainPipeline<>(this.source, this.mapper, this.throwingMapper, null,
				ThrowingRunnable.ignore());
		return pipline.onClose(this.closeable);
	}

	/**
	 * 注册针对目标值的关闭消费回调，返回支持该回调的{@link Pipeline}。 回调会在流水线关闭时执行，用于对映射后的目标值（V类型）进行清理。
	 *
	 * @param closer 目标值的关闭消费操作，接收V类型值并可能抛出T类型异常，不可为null
	 * @return 包含该关闭回调的{@link Pipeline}实例
	 */
	@Override
	public Pipeline<V, T> onClose(@NonNull ThrowingConsumer<? super V, ? extends T> closer) {
		return closeable().onClose(closer);
	}

	/**
	 * 注册无参的关闭回调，返回支持该回调的{@link Pipeline}。 回调会在流水线关闭时执行，用于补充额外的清理逻辑（如日志记录、资源汇总等）。
	 *
	 * @param closeable 无参的关闭操作，可能抛出T类型异常，不可为null
	 * @return 包含该关闭回调的{@link Pipeline}实例
	 */
	@Override
	public Pipeline<V, T> onClose(@NonNull ThrowingRunnable<? extends T> closeable) {
		return closeable().onClose(closeable);
	}
}