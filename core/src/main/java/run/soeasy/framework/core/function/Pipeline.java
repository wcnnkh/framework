package run.soeasy.framework.core.function;

import java.io.Closeable;
import java.io.IOException;
import java.util.function.Function;

import lombok.NonNull;

/**
 * 流水线接口，扩展自{@link ThrowingSupplier}，支持资源的链式处理与自动关闭。
 * 提供了资源获取、转换、关闭等一体化操作，适用于需要按步骤处理资源且需确保资源释放的场景。
 * 
 * <p>核心功能：
 * <ul>
 * <li>资源链式处理：通过{@link #map(ThrowingFunction)}实现资源的转换与传递</li>
 * <li>自动关闭支持：集成资源关闭机制，确保资源使用后正确释放</li>
 * <li>异常转换：通过{@link #throwing(Function)}统一处理不同类型的异常</li>
 * <li>适配标准资源：提供静态方法支持{@link AutoCloseable}和{@link Closeable}类型资源</li>
 * </ul>
 *
 * @param <T> 流水线处理的资源类型
 * @param <E> 操作中可能抛出的异常类型，必须是{@link Throwable}的子类
 * @see ThrowingSupplier
 * @see Pool
 */
public interface Pipeline<T, E extends Throwable> extends ThrowingSupplier<T, E> {

    /**
     * 创建一个空的流水线实例，不包含任何资源
     *
     * @param <T> 资源类型
     * @param <E> 异常类型
     * @return 空流水线实例
     */
    @SuppressWarnings("unchecked")
    public static <T, E extends Throwable> Pipeline<T, E> empty() {
        return (Pipeline<T, E>) EmptyPipeline.INSTANCE;
    }

    /**
     * 基于资源供应者创建流水线实例
     *
     * @param <T> 资源类型
     * @param <E> 异常类型
     * @param supplier 资源供应者，非空
     * @return 新的流水线实例
     */
    public static <T, E extends Throwable> Pipeline<T, E> forSupplier(ThrowingSupplier<T, E> supplier) {
        return supplier.closeable();
    }

    /**
     * 为支持自动关闭的资源创建流水线（适配{@link AutoCloseable}）
     * 流水线会在资源使用后自动调用{@link AutoCloseable#close()}释放资源
     *
     * @param <T> 资源类型，必须实现{@link AutoCloseable}
     * @param autoCloseableSupplier 自动关闭资源的供应者，非空
     * @return 支持自动关闭的流水线实例
     */
    public static <T extends AutoCloseable> Pipeline<T, Exception> forAutoCloseable(
            ThrowingSupplier<T, Exception> autoCloseableSupplier) {
        return autoCloseableSupplier.onClose(AutoCloseable::close).closeable();
    }

    /**
     * 为可关闭资源创建流水线（适配{@link Closeable}）
     * 流水线会在资源使用后自动调用{@link Closeable#close()}释放资源
     *
     * @param <T> 资源类型，必须实现{@link Closeable}
     * @param closeableSupplier 可关闭资源的供应者，非空
     * @return 支持自动关闭的流水线实例
     */
    public static <T extends Closeable> Pipeline<T, IOException> forCloseable(
            ThrowingSupplier<T, IOException> closeableSupplier) {
        return closeableSupplier.onClose(Closeable::close).closeable();
    }

    /**
     * 创建支持自动关闭的资源供应者，获取资源后会在适当时候自动关闭
     *
     * @return 支持自动关闭的{@link ThrowingSupplier}实例
     */
    default ThrowingSupplier<T, E> autoCloseable() {
        return new ChainPipeline<>(this, ThrowingFunction.identity(), ThrowingConsumer.ignore(), Function.identity(),
                false, this::close);
    }

    /**
     * 关闭当前流水线，释放相关资源
     *
     * @throws E 关闭过程中可能抛出的异常
     */
    void close() throws E;

    /**
     * 返回当前流水线实例，支持方法链编程风格
     *
     * @return 当前流水线实例
     */
    @Override
    default Pipeline<T, E> closeable() {
        return this;
    }

    /**
     * 判断流水线是否已关闭
     *
     * @return true表示已关闭，false表示未关闭
     */
    boolean isClosed();

    /**
     * 对流水线中的资源进行映射转换，返回新的流水线
     * 新流水线会继承原流水线的关闭机制
     *
     * @param <R> 转换后的资源类型
     * @param pipeline 转换函数，非空，接收当前资源并返回转换后的资源
     * @return 包含转换后资源的新流水线
     */
    @Override
    default <R> Pipeline<R, E> map(@NonNull ThrowingFunction<? super T, ? extends R, E> pipeline) {
        return new ChainPipeline<>(this, pipeline, ThrowingConsumer.ignore(), Function.identity(), false,
                ThrowingRunnable.ignore());
    }

    /**
     * 注册资源关闭时的消费回调，返回支持资源池操作的{@link Pool}实例
     *
     * @param consumer 资源关闭时的消费操作，非空
     * @return 包含关闭回调的{@link Pool}实例
     */
    @Override
    default Pool<T, E> onClose(@NonNull ThrowingConsumer<? super T, ? extends E> consumer) {
        return new ChainPool<>(this.autoCloseable(), Function.identity(), consumer);
    }

    /**
     * 注册资源关闭时的操作。当流水线关闭时，会调用指定的closeable操作。
     *
     * @param closeable 关闭操作，不可为null
     * @return 注册操作后的流水线实例
     */
    @Override
    default Pipeline<T, E> onClose(@NonNull ThrowingRunnable<? extends E> closeable) {
        return new ChainPipeline<>(this, ThrowingFunction.identity(), ThrowingConsumer.ignore(), Function.identity(),
                false, closeable);
    }

    /**
     * 创建单例模式的流水线，缓存资源实例。首次获取资源后会缓存结果，后续调用get()返回相同实例。
     *
     * @return 单例模式的流水线实例
     */
    @Override
    default Pipeline<T, E> singleton() {
        return new ChainPipeline<>(this, ThrowingFunction.identity(), ThrowingConsumer.ignore(), Function.identity(),
                true, this::close);
    }

    /**
     * 转换异常类型，返回新的流水线实例。允许将原始异常E转换为新的异常类型R，实现异常类型的统一处理。
     *
     * @param <R>            新的异常类型，必须是Throwable的子类
     * @param throwingMapper 异常转换函数，不可为null
     * @return 异常转换后的流水线实例
     */
    @Override
    default <R extends Throwable> Pipeline<T, R> throwing(@NonNull Function<? super E, ? extends R> throwingMapper) {
        return new ChainPipeline<>(this, ThrowingFunction.identity(), ThrowingConsumer.ignore(), throwingMapper, false,
                this::close);
    }
}