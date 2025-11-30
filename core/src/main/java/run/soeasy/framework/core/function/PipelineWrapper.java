package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.NonNull;

/**
 * 流水线包装器接口，继承自{@link Pipeline}和{@link ThrowingSupplierWrapper}，用于对{@link Pipeline}实例进行包装或代理。
 * 该接口通过{@link #getSource()}获取被包装的原始流水线实例，并将所有方法调用转发给原始实例，
 * 适用于需要对流水线功能进行增强、拦截或适配的场景（如添加日志、权限校验等横切逻辑）。
 *
 * @param <T> 资源类型，与被包装的流水线一致
 * @param <E> 异常类型，与被包装的流水线一致
 * @param <W> 被包装的流水线类型，必须是{@link Pipeline}的子类型
 * @see Pipeline
 * @see ThrowingSupplierWrapper
 */
@FunctionalInterface
public interface PipelineWrapper<T, E extends Throwable, W extends Pipeline<T, E>>
		extends Pipeline<T, E>, ThrowingSupplierWrapper<T, E, W> {

	/**
	 * 获取包装的源流水线实例，是所有委托操作的基础。
	 * 实现类需通过该方法返回被包装的原始流水线，确保所有方法调用能正确转发。
	 *
	 * @return 被包装的源流水线实例（非空）
	 */
	@Override
	W getSource();

	/**
	 * 创建自动关闭的资源供应者，委托给源流水线的{@link Pipeline#autoCloseable()}方法。
	 * 行为与源流水线的对应方法完全一致。
	 *
	 * @return 自动关闭的{@link ThrowingSupplier}实例，由源流水线提供
	 */
	@Override
	default ThrowingSupplier<T, E> autoCloseable() {
		return getSource().autoCloseable();
	}

	/**
	 * 关闭资源，委托给源流水线的{@link Pipeline#close()}方法。
	 * 执行源流水线的关闭逻辑，释放关联资源。
	 *
	 * @throws E 关闭过程中可能抛出的异常，与源流水线一致
	 */
	@Override
	default void close() throws E {
		getSource().close();
	}

	/**
	 * 标识当前流水线支持关闭功能，返回源流水线的{@link Pipeline#closeable()}结果。
	 * 用于链式调用时保持接口一致性，实际行为由源流水线决定。
	 *
	 * @return 源流水线的{@link Pipeline#closeable()}返回值
	 */
	@Override
	default Pipeline<T, E> closeable() {
		return getSource().closeable();
	}

	/**
	 * 检查流水线是否已关闭，委托给源流水线的{@link Pipeline#isClosed()}方法。
	 * 返回源流水线的关闭状态。
	 *
	 * @return true表示源流水线已关闭，false表示未关闭
	 */
	@Override
	default boolean isClosed() {
		return getSource().isClosed();
	}

	/**
	 * 对资源进行映射转换，委托给源流水线的{@link Pipeline#map(ThrowingFunction)}方法。
	 * 转换逻辑由源流水线实现，返回新的映射后流水线。
	 *
	 * @param <R>      转换后的资源类型
	 * @param mapper 映射函数，接收当前资源并返回转换后资源，非空
	 * @return 源流水线执行映射后的{@link Pipeline}实例
	 */
	@Override
	default <R> Pipeline<R, E> map(@NonNull ThrowingFunction<? super T, ? extends R, E> mapper) {
		return getSource().map(mapper);
	}

	/**
	 * 注册资源关闭时的消费回调，委托给源流水线的{@link Pipeline#onClose(ThrowingConsumer)}方法。
	 * 回调会在源流水线关闭时执行。
	 *
	 * @param consumer 资源关闭时的消费操作，非空
	 * @return 源流水线注册回调后的{@link Pipeline}实例
	 */
	@Override
	default Pipeline<T, E> onClose(@NonNull ThrowingConsumer<? super T, ? extends E> consumer) {
		return getSource().onClose(consumer);
	}

	/**
	 * 注册资源关闭时的无参回调，委托给源流水线的{@link Pipeline#onClose(ThrowingRunnable)}方法。
	 * 回调会在源流水线关闭时执行。
	 *
	 * @param closeable 关闭操作，非空
	 * @return 源流水线注册回调后的{@link Pipeline}实例
	 */
	@Override
	default Pipeline<T, E> onClose(@NonNull ThrowingRunnable<? extends E> closeable) {
		return getSource().onClose(closeable);
	}

	/**
	 * 转换异常类型，委托给源流水线的{@link Pipeline#throwing(Function)}方法。
	 * 将源流水线的异常类型转换为目标类型。
	 *
	 * @param <R>            新的异常类型，必须是{@link Throwable}的子类
	 * @param throwingMapper 异常转换函数，非空
	 * @return 源流水线执行异常转换后的{@link Pipeline}实例
	 */
	@Override
	default <R extends Throwable> Pipeline<T, R> throwing(@NonNull Function<? super E, ? extends R> throwingMapper) {
		return getSource().throwing(throwingMapper);
	}

	/**
	 * 将资源包装为支持异常处理的{@link ThrowingOptional}，委托给源流水线的{@link Pipeline#optional()}方法。
	 * 返回源流水线提供的Optional容器。
	 *
	 * @return 源流水线的{@link ThrowingOptional}实例
	 */
	@Override
	default ThrowingOptional<T, E> optional() {
		return getSource().optional();
	}
}