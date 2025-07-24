package run.soeasy.framework.core.function;

import java.io.Closeable;
import java.io.IOException;
import java.util.function.Function;

import lombok.NonNull;

/**
 * 资源管理流水线接口，扩展ThrowingSupplier实现可抛异常的资源供应与生命周期管理。
 * 该接口定义了资源获取、转换、释放的链式操作规范，支持自动关闭机制和异常处理，
 * 适用于需要统一管理资源生命周期的函数式编程场景。
 *
 * <p>核心特性：
 * <ul>
 *   <li>资源生命周期管理：提供close()方法释放资源，支持try-with-resources</li>
 *   <li>链式操作支持：通过map/onClose等方法实现函数式流水线操作</li>
 *   <li>自动关闭适配：支持AutoCloseable/Closeable资源的自动关闭注册</li>
 *   <li>异常转换机制：通过throwing方法实现异常类型的统一转换</li>
 *   <li>单例模式支持：通过singleton()方法实现结果缓存</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>IO资源管理（文件、流等）</li>
 *   <li>数据库连接/会话管理</li>
 *   <li>网络连接等需要显式释放的资源</li>
 *   <li>需要统一异常处理的资源获取流程</li>
 *   <li>复杂资源转换的链式操作场景</li>
 * </ul>
 *
 * @param <T> 流水线处理的资源类型
 * @param <E> 可能抛出的异常类型，必须是Throwable的子类
 * @see ThrowingSupplier
 * @see AutoCloseable
 * @see Closeable
 */
public interface Pipeline<T, E extends Throwable> extends ThrowingSupplier<T, E> {

    /**
     * 获取空的流水线实例。
     * 空流水线的get()方法会抛出UnsupportedOperationException，close()无操作。
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
     * 基于ThrowingSupplier创建流水线实例。
     * 自动注册默认关闭回调（无操作），适用于不需要显式关闭的资源。
     *
     * @param <T>      资源类型
     * @param <E>      异常类型
     * @param supplier 资源供应者
     * @return 包装后的流水线实例
     */
    public static <T, E extends Throwable> Pipeline<T, E> forSupplier(ThrowingSupplier<T, E> supplier) {
        return supplier.closeable();
    }

    /**
     * 为AutoCloseable资源创建流水线实例。
     * 自动注册close()回调，适用于实现了AutoCloseable接口的资源。
     *
     * @param <T>                  资源类型，必须是AutoCloseable的子类型
     * @param autoCloseableSupplier AutoCloseable资源供应者
     * @return 包装后的流水线实例，支持自动关闭
     * @throws Exception 资源获取或关闭时可能抛出的异常
     */
    public static <T extends AutoCloseable> Pipeline<T, Exception> forAutoCloseable(
            ThrowingSupplier<T, Exception> autoCloseableSupplier) {
        return autoCloseableSupplier.onClose(AutoCloseable::close).autoCloseable().closeable();
    }

    /**
     * 为Closeable资源创建流水线实例。
     * 自动注册close()回调，适用于实现了Closeable接口的资源。
     *
     * @param <T>               资源类型，必须是Closeable的子类型
     * @param closeableSupplier Closeable资源供应者
     * @return 包装后的流水线实例，支持自动关闭
     * @throws IOException 资源获取或关闭时可能抛出的IO异常
     */
    public static <T extends Closeable> Pipeline<T, IOException> forCloseable(
            ThrowingSupplier<T, IOException> closeableSupplier) {
        return closeableSupplier.onClose(Closeable::close).autoCloseable().closeable();
    }

    /**
     * 将当前流水线标记为自动关闭，注册当前close()方法为关闭回调。
     * 返回的供应者在获取资源后会自动调用当前流水线的close()方法。
     *
     * @return 自动关闭的供应者实例
     */
    default ThrowingSupplier<T, E> autoCloseable() {
        return new MappingThrowingSupplier<>(this, ThrowingFunction.identity(), ThrowingConsumer.ignore(),
                Function.identity(), false, this::close);
    }

    /**
     * 释放流水线管理的资源。
     * 该方法由try-with-resources自动调用，或手动调用以释放资源。
     *
     * @throws E 资源释放时可能抛出的异常
     */
    void close() throws E;

    /**
     * 标识当前流水线已支持关闭功能，返回自身。
     * 用于链式调用时保持接口一致性。
     *
     * @return 当前流水线实例
     */
    @Override
    default Pipeline<T, E> closeable() {
        return this;
    }

    /**
     * 检查流水线是否已关闭。
     * 返回true表示资源已释放，后续操作可能无效。
     *
     * @return 流水线关闭状态
     */
    boolean isClosed();

    /**
     * 对资源进行映射转换，返回新的流水线实例。
     * 支持函数式编程的map操作，将T类型资源转换为R类型。
     *
     * @param <R>    转换后的资源类型
     * @param pipeline 映射函数，不可为null
     * @return 转换后的流水线实例
     */
    @Override
    default <R> Pipeline<R, E> map(@NonNull ThrowingFunction<? super T, ? extends R, E> pipeline) {
        return new MappingPipeline<>(this, pipeline, ThrowingConsumer.ignore(), Function.identity(), false,
                ThrowingRunnable.ignore());
    }

    /**
     * 注册资源关闭时的回调函数。
     * 当流水线关闭时，会调用指定的consumer处理资源。
     *
     * @param consumer 关闭回调函数，不可为null
     * @return 注册回调后的流水线实例
     */
    @Override
    default Pipeline<T, E> onClose(@NonNull ThrowingConsumer<? super T, ? extends E> consumer) {
        return new MappingPipeline<>(this, ThrowingFunction.identity(), consumer, Function.identity(), false,
                ThrowingRunnable.ignore());
    }

    /**
     * 注册资源关闭时的操作。
     * 当流水线关闭时，会调用指定的closeable操作。
     *
     * @param closeable 关闭操作，不可为null
     * @return 注册操作后的流水线实例
     */
    @Override
    default Pipeline<T, E> onClose(@NonNull ThrowingRunnable<? extends E> closeable) {
        return new MappingPipeline<>(this, ThrowingFunction.identity(), ThrowingConsumer.ignore(), Function.identity(),
                false, closeable);
    }

    /**
     * 创建单例模式的流水线，缓存资源实例。
     * 首次获取资源后会缓存结果，后续调用get()返回相同实例。
     *
     * @return 单例模式的流水线实例
     */
    @Override
    default Pipeline<T, E> singleton() {
        return new MappingPipeline<>(this, ThrowingFunction.identity(), ThrowingConsumer.ignore(), Function.identity(),
                true, ThrowingRunnable.ignore());
    }

    /**
     * 转换异常类型，返回新的流水线实例。
     * 允许将原始异常E转换为新的异常类型R，实现异常类型的统一处理。
     *
     * @param <R>               新的异常类型，必须是Throwable的子类
     * @param throwingMapper    异常转换函数，不可为null
     * @return 异常转换后的流水线实例
     */
    @Override
    default <R extends Throwable> Pipeline<T, R> throwing(@NonNull Function<? super E, ? extends R> throwingMapper) {
        return new MappingPipeline<>(this, ThrowingFunction.identity(), ThrowingConsumer.ignore(), throwingMapper,
                false, ThrowingRunnable.ignore());
    }
}