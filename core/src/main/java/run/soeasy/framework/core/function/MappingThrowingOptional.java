package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.NonNull;

/**
 * 映射型可抛出异常的Optional实现，支持将源供应者的值进行映射转换，
 * 并提供Optional风格的空值安全操作，同时支持异常类型转换和资源管理。
 * 该类继承自{@link MappingThrowingSupplier}并实现{@link ThrowingOptional}接口，
 * 兼具供应者和Optional的双重特性，适用于需要类型转换和异常处理的函数式场景。
 *
 * <p>核心特性：
 * <ul>
 *   <li>空值安全：遵循Optional模式，支持安全的值访问和转换</li>
 *   <li>类型映射：通过{@link ThrowingFunction}将源类型S转换为目标类型V</li>
 *   <li>异常封装：使用{@link Function}将源异常E转换为目标异常T</li>
 *   <li>资源管理：通过endpoint参数支持值使用后的资源清理</li>
 *   <li>单例模式：支持配置为单例模式，确保值仅获取一次</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>需要安全处理可能为null的值并抛出异常的场景</li>
 *   <li>函数式编程中需要链式调用和异常处理的Optional操作</li>
 *   <li>微服务间数据格式转换时的异常标准化处理</li>
 *   <li>需要缓存计算结果避免重复获取的场景</li>
 * </ul>
 *
 * @param <S> 源供应者提供的原始类型
 * @param <V> 映射后的目标类型
 * @param <E> 源供应者可能抛出的异常类型
 * @param <T> 最终抛出的目标异常类型
 * @param <W> 源供应者类型，需实现{@link ThrowingSupplier}接口
 * @see ThrowingOptional
 * @see MappingThrowingSupplier
 */
class MappingThrowingOptional<S, V, E extends Throwable, T extends Throwable, W extends ThrowingSupplier<S, E>>
        extends MappingThrowingSupplier<S, V, E, T, W> implements ThrowingOptional<V, T> {

    /**
     * 构造映射型可抛出异常的Optional实例。
     *
     * @param source         源供应者，不可为null
     * @param mapper         类型映射函数，将S转换为V，不可为null
     * @param endpoint       资源清理消费者，值使用后执行，可为null
     * @param throwingMapper 异常转换函数，不可为null
     * @param singleton      是否启用单例模式
     * @param closeable      关闭操作执行器，不可为null
     */
    public MappingThrowingOptional(@NonNull W source, 
                                  @NonNull ThrowingFunction<? super S, ? extends V, T> mapper,
                                  ThrowingConsumer<? super S, ? extends E> endpoint,
                                  @NonNull Function<? super E, ? extends T> throwingMapper,
                                  boolean singleton, 
                                  @NonNull ThrowingRunnable<? extends T> closeable) {
        super(source, mapper, endpoint, throwingMapper, singleton, closeable);
    }

    /**
     * 对值进行扁平映射转换，返回映射后的结果。
     * 若当前值为null，该方法不会执行映射函数，直接返回null。
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
     * 添加额外的映射转换，返回新的MappingThrowingOptional实例。
     * 新实例会先应用当前映射函数，再应用指定的映射函数。
     *
     * @param <R>    新的目标类型
     * @param mapper 额外的映射函数，不可为null
     * @return 新的MappingThrowingOptional实例
     */
    @Override
    public <R> MappingThrowingOptional<S, R, E, T, W> map(@NonNull ThrowingFunction<? super V, ? extends R, T> mapper) {
        return new MappingThrowingOptional<>(this.source, this.mapper.andThen(mapper), endpoint, throwingMapper,
                singleton, closeable);
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