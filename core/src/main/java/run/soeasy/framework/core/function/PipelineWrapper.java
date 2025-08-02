package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.NonNull;

/**
 * 流水线包装器接口，用于包装{@link Pipeline}实例，提供统一的委托操作机制。
 * 该接口继承自{@link Pipeline}和{@link ThrowingSupplierWrapper}，
 * 允许将任意流水线实例包装为具有相同接口的实例，同时保留源流水线的所有功能。
 *
 * <p>核心特性：
 * <ul>
 *   <li>透明委托：所有方法调用均转发至源流水线实例，保持行为一致性</li>
 *   <li>类型安全：通过泛型参数确保包装前后的类型一致性</li>
 *   <li>无额外逻辑：仅作为包装层，不修改源流水线的任何行为</li>
 *   <li>可扩展接口：允许通过继承添加额外功能而不修改源实现</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>为流水线添加日志记录功能</li>
 *   <li>实现流水线的访问控制或权限校验</li>
 *   <li>包装第三方流水线实现以符合框架接口</li>
 *   <li>在不修改原始实现的情况下添加监控统计功能</li>
 * </ul>
 *
 * @param <T> 流水线处理的资源类型
 * @param <E> 可能抛出的异常类型，必须是Throwable的子类
 * @param <W> 具体的包装器类型，需实现当前接口
 * @see Pipeline
 * @see ThrowingSupplierWrapper
 */
@FunctionalInterface
public interface PipelineWrapper<T, E extends Throwable, W extends Pipeline<T, E>>
        extends Pipeline<T, E>, ThrowingSupplierWrapper<T, E, W> {

    /**
     * 获取包装的源流水线实例。
     * 该方法由{@link ThrowingSupplierWrapper}接口定义，
     * 是所有委托操作的基础。
     *
     * @return 被包装的源流水线实例
     */
    @Override
    W getSource();

    /**
     * 创建自动关闭的供应者，委托给源流水线的对应方法。
     *
     * @return 自动关闭的供应者实例
     */
    @Override
    default ThrowingSupplier<T, E> autoCloseable() {
        return getSource().autoCloseable();
    }

    /**
     * 关闭资源，委托给源流水线的{@link Pipeline#close}方法。
     *
     * @throws E 资源关闭时可能抛出的异常
     */
    @Override
    default void close() throws E {
        getSource().close();
    }

    /**
     * 标识当前流水线已支持关闭功能，返回源流水线的对应实例。
     *
     * @return 源流水线实例
     */
    @Override
    default Pipeline<T, E> closeable() {
        return getSource().closeable();
    }

    /**
     * 检查流水线是否已关闭，委托给源流水线的对应方法。
     *
     * @return 源流水线的关闭状态
     */
    @Override
    default boolean isClosed() {
        return getSource().isClosed();
    }

    /**
     * 对资源进行映射转换，委托给源流水线的{@link Pipeline#map}方法。
     *
     * @param <R>    转换后的资源类型
     * @param pipeline 映射函数，不可为null
     * @return 映射后的流水线实例
     */
    @Override
    default <R> Pipeline<R, E> map(@NonNull ThrowingFunction<? super T, ? extends R, E> pipeline) {
        return getSource().map(pipeline);
    }

    /**
     * 注册资源关闭时的回调函数，委托给源流水线的对应方法。
     *
     * @param consumer 关闭回调函数，不可为null
     * @return 注册回调后的流水线实例
     */
    @Override
    default Pool<T, E> onClose(@NonNull ThrowingConsumer<? super T, ? extends E> consumer) {
        return getSource().onClose(consumer);
    }

    /**
     * 注册资源关闭时的操作，委托给源流水线的对应方法。
     *
     * @param closeable 关闭操作，不可为null
     * @return 注册操作后的流水线实例
     */
    @Override
    default Pipeline<T, E> onClose(@NonNull ThrowingRunnable<? extends E> closeable) {
        return getSource().onClose(closeable);
    }

    /**
     * 创建单例模式的流水线，委托给源流水线的对应方法。
     *
     * @return 单例模式的流水线实例
     */
    @Override
    default Pipeline<T, E> singleton() {
        return getSource().singleton();
    }

    /**
     * 转换异常类型，委托给源流水线的{@link Pipeline#throwing}方法。
     *
     * @param <R>               新的异常类型，必须是Throwable的子类
     * @param throwingMapper    异常转换函数，不可为null
     * @return 异常转换后的流水线实例
     */
    @Override
    default <R extends Throwable> Pipeline<T, R> throwing(@NonNull Function<? super E, ? extends R> throwingMapper) {
        return getSource().throwing(throwingMapper);
    }
}