package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.NonNull;

/**
 * 资源池接口，继承自{@link ThrowingSupplier}，专注于资源的生命周期管理，提供资源获取与关闭的统一接口。
 * 适用于需要重复使用或需显式释放的资源（如数据库连接、网络连接等），支持通过链式操作组合资源处理逻辑。
 * 
 * <p>核心能力：
 * <ul>
 * <li>资源获取：通过继承的{@link #get()}方法获取资源</li>
 * <li>资源关闭：通过{@link #close(Object)}方法释放资源</li>
 * <li>链式扩展：支持资源转换、异常处理、关闭回调等操作的链式调用</li>
 * <li>自动管理：结合{@link Pipeline}实现资源使用后的自动关闭</li>
 * </ul>
 *
 * @param <T> 资源的类型
 * @param <E> 操作中可能抛出的异常类型，必须是{@link Throwable}的子类
 * @see ThrowingSupplier
 * @see Pipeline
 */
public interface Pool<T, E extends Throwable> extends ThrowingSupplier<T, E> {

    /**
     * 关闭指定资源，释放其占用的系统资源。
     * 该方法是资源池的核心清理操作，应在资源使用完毕后调用，确保资源正确释放。
     *
     * @param source 需要关闭的资源实例，非空
     * @throws E 关闭资源过程中可能抛出的异常（如IO异常、连接关闭异常等）
     */
    void close(T source) throws E;

    /**
     * 对资源进行映射转换，并返回支持自动关闭的{@link Pipeline}。
     * 映射后的流水线会在操作完成后自动调用当前Pool的{@link #close(Object)}方法关闭原始资源，避免资源泄漏。
     *
     * @param <R> 映射后的资源类型
     * @param mapper 用于转换资源的函数，接收当前资源并返回转换后的资源，非空且可能抛出异常{@code E}
     * @return 包含转换后资源的{@link Pipeline}实例，支持后续操作并自动管理资源生命周期
     */
    @Override
    default <R> Pipeline<R, E> map(@NonNull ThrowingFunction<? super T, ? extends R, E> mapper) {
        return new ChainPipeline<>(this, mapper, this::close, Function.identity(), true, ThrowingRunnable.ignore());
    }

    /**
     * 注册资源关闭时的额外消费回调，返回新的{@link Pool}实例。
     * 新实例在执行{@link #close(Object)}时，会先调用原有关闭逻辑，再执行注册的消费回调，实现关闭增强。
     *
     * @param consumer 资源关闭时的消费操作，接收待关闭的资源并可能抛出异常{@code E}，非空
     * @return 包含新关闭逻辑的{@link Pool}实例
     */
    @Override
    default Pool<T, E> onClose(@NonNull ThrowingConsumer<? super T, ? extends E> consumer) {
        return new OnClosePool<>(this, Function.identity(), consumer);
    }

    /**
     * 注册资源关闭时的无参回调，返回支持链式操作的{@link Pipeline}。
     * 该流水线在关闭时，会先通过当前Pool的{@link #close(Object)}方法关闭资源，再执行注册的无参回调。
     *
     * @param closeable 资源关闭后执行的无参操作，可能抛出异常{@code E}，非空
     * @return 包含关闭回调的{@link Pipeline}实例
     */
    @Override
    default Pipeline<T, E> onClose(@NonNull ThrowingRunnable<? extends E> closeable) {
        return new ChainPipeline<>(this, ThrowingFunction.identity(), this::close, Function.identity(), true,
                closeable);
    }

    /**
     * 创建支持自动关闭的{@link Pipeline}实例。
     * 该流水线在资源使用完毕后，会自动调用当前Pool的{@link #close(Object)}方法关闭资源，简化资源管理。
     *
     * @return 支持自动关闭的{@link Pipeline}实例
     */
    @Override
    default Pipeline<T, E> closeable() {
        return new ChainPipeline<>(this, ThrowingFunction.identity(), this::close, Function.identity(), true,
                ThrowingRunnable.ignore());
    }

    /**
     * 转换异常类型，返回支持新异常类型的{@link Pool}实例。
     * 通过异常转换函数将原始异常{@code E}转换为新类型{@code R}，便于统一异常处理。
     *
     * @param <R> 新的异常类型，必须是{@link Throwable}的子类
     * @param throwingMapper 异常转换函数，接收原始异常并返回新类型异常，非空
     * @return 异常类型转换后的{@link Pool}实例
     */
    @Override
    default <R extends Throwable> Pool<T, R> throwing(@NonNull Function<? super E, ? extends R> throwingMapper) {
        return new ChainPool<>(this, throwingMapper, this::close);
    }

    /**
     * 创建单例模式的{@link Pipeline}，确保资源只被获取一次并缓存。
     * 首次调用{@link #get()}获取资源后会缓存结果，后续调用返回相同实例，适用于复用性资源。
     *
     * @return 单例模式的{@link Pipeline}实例，支持自动关闭
     */
    @Override
    default Pipeline<T, E> singleton() {
        return closeable();
    }

    /**
     * 将资源包装为支持异常处理的{@link ThrowingOptional}，便于空值安全处理。
     * 包装后的Optional会通过自动关闭流水线管理资源生命周期，确保资源正确释放。
     *
     * @return 包含资源的{@link ThrowingOptional}实例
     */
    @Override
    default ThrowingOptional<T, E> optional() {
        return closeable().autoCloseable().optional();
    }
}
    