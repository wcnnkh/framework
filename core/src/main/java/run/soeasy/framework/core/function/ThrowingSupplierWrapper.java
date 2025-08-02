package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.NonNull;
import run.soeasy.framework.core.domain.Wrapper;

/**
 * 可抛出异常的供应者包装器接口，用于包装{@link ThrowingSupplier}实例，
 * 提供统一的委托操作机制。该接口继承自{@link ThrowingSupplier}和{@link Wrapper}，
 * 允许将任意供应者实例包装为具有相同接口的实例，同时保留源供应者的所有功能。
 *
 * <p>
 * 核心特性：
 * <ul>
 * <li>透明委托：所有方法调用均转发至源供应者实例，保持行为一致性</li>
 * <li>类型安全：通过泛型参数确保包装前后的类型一致性</li>
 * <li>资源管理：支持将供应者转换为可关闭的流水线</li>
 * <li>函数组合：支持与其他函数进行组合操作（map）</li>
 * <li>异常转换：支持统一转换异常类型</li>
 * <li>单例模式：支持将供应者配置为单例模式</li>
 * </ul>
 *
 * <p>
 * 使用场景：
 * <ul>
 * <li>为供应者添加日志记录功能</li>
 * <li>实现供应者的访问控制或权限校验</li>
 * <li>包装第三方供应者实现以符合框架接口</li>
 * <li>在不修改原始实现的情况下添加监控统计功能</li>
 * <li>统一不同模块的异常处理逻辑</li>
 * </ul>
 *
 * @param <T> 供应者提供的值类型
 * @param <E> 可能抛出的异常类型，必须是Throwable的子类
 * @param <W> 具体的包装器类型，需实现当前接口
 * @see ThrowingSupplier
 * @see Wrapper
 */
public interface ThrowingSupplierWrapper<T, E extends Throwable, W extends ThrowingSupplier<T, E>>
		extends ThrowingSupplier<T, E>, Wrapper<W> {

	/**
	 * 获取包装的源供应者实例。 该方法由{@link Wrapper}接口定义，是所有委托操作的基础。
	 *
	 * @return 被包装的源供应者实例
	 */
	@Override
	W getSource();

	/**
	 * 将当前供应者转换为可关闭的流水线，委托给源供应者的对应方法。
	 *
	 * @return 可关闭的流水线实例
	 */
	@Override
	default Pipeline<T, E> closeable() {
		return getSource().closeable();
	}

	/**
	 * 注册资源关闭时的回调函数，委托给源供应者的对应方法。
	 *
	 * @param consumer 关闭回调函数，不可为null
	 * @return 注册回调后的流水线实例
	 */
	@Override
	default Pool<T, E> onClose(@NonNull ThrowingConsumer<? super T, ? extends E> consumer) {
		return getSource().onClose(consumer);
	}

	/**
	 * 注册资源关闭时的操作，委托给源供应者的对应方法。
	 *
	 * @param endpoint 关闭操作，不可为null
	 * @return 注册操作后的流水线实例
	 */
	@Override
	default Pipeline<T, E> onClose(@NonNull ThrowingRunnable<? extends E> endpoint) {
		return getSource().onClose(endpoint);
	}

	/**
	 * 对供应的值进行映射转换，委托给源供应者的{@link ThrowingSupplier#map}方法。
	 *
	 * @param <R>    映射后的结果类型
	 * @param mapper 映射函数，不可为null
	 * @return 映射后的供应者实例
	 */
	@Override
	default <R> ThrowingSupplier<R, E> map(@NonNull ThrowingFunction<? super T, ? extends R, E> mapper) {
		return getSource().map(mapper);
	}

	/**
	 * 转换异常类型，委托给源供应者的对应方法。
	 *
	 * @param <R>            新的异常类型，必须是Throwable的子类
	 * @param throwingMapper 异常转换函数，不可为null
	 * @return 异常类型转换后的供应者实例
	 */
	@Override
	default <R extends Throwable> ThrowingSupplier<T, R> throwing(
			@NonNull Function<? super E, ? extends R> throwingMapper) {
		return getSource().throwing(throwingMapper);
	}

	/**
	 * 获取供应的值，委托给源供应者的{@link ThrowingSupplier#get}方法。
	 *
	 * @return 供应的值
	 * @throws E 可能抛出的指定类型异常
	 */
	@Override
	default T get() throws E {
		return getSource().get();
	}

	/**
	 * 将当前供应者转换为可抛出异常的Optional，委托给源供应者的对应方法。
	 *
	 * @return 可抛出异常的Optional实例
	 */
	@Override
	default ThrowingOptional<T, E> optional() {
		return getSource().optional();
	}

	/**
	 * 将当前供应者配置为单例模式，委托给源供应者的对应方法。
	 *
	 * @return 单例模式的流水线实例
	 */
	@Override
	default ThrowingSupplier<T, E> singleton() {
		return getSource().singleton();
	}
}