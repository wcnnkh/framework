package run.soeasy.framework.core.function;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 映射型可抛出异常的供应者实现，支持将源供应者提供的值进行映射转换，
 * 并在值使用完毕后执行资源清理操作，同时支持异常类型转换。
 * 该实现实现了{@link Pipeline}接口，提供资源管理和自动关闭功能。
 *
 * <p>核心特性：
 * <ul>
 *   <li>值映射：通过{@link ThrowingFunction}将源值转换为目标类型</li>
 *   <li>资源管理：支持在值使用后执行清理操作（如关闭文件、释放连接等）</li>
 *   <li>异常转换：通过{@link Function}将源异常类型转换为目标异常类型</li>
 *   <li>单例模式：支持配置为单例模式，确保值仅被获取一次</li>
 *   <li>线程安全：在单例模式下使用双重检查锁确保值获取的原子性</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>需要将一种资源类型转换为另一种资源类型的场景</li>
 *   <li>资源使用后需要自动释放的场景</li>
 *   <li>统一不同模块抛出的异常类型</li>
 *   <li>需要缓存计算结果避免重复计算的场景</li>
 * </ul>
 *
 * @param <S> 源供应者提供的值类型
 * @param <V> 映射后的值类型
 * @param <E> 源供应者可能抛出的异常类型
 * @param <T> 目标异常类型，源异常会被转换为此类型
 * @param <W> 源供应者类型，必须实现{@link ThrowingSupplier}接口
 * @see Pipeline
 * @see ThrowingSupplier
 */
@RequiredArgsConstructor
class MappingThrowingSupplier<S, V, E extends Throwable, T extends Throwable, W extends ThrowingSupplier<? extends S, ? extends E>>
        implements Pipeline<V, T> {
    
    /**
     * 源供应者，用于获取初始值。
     */
    @NonNull
    protected final W source;
    
    /**
     * 值映射函数，将源值转换为目标类型。
     */
    @NonNull
    protected final ThrowingFunction<? super S, ? extends V, T> mapper;
    
    /**
     * 资源清理消费者，在值使用完毕后执行清理操作。
     */
    protected final ThrowingConsumer<? super S, ? extends E> endpoint;
    
    /**
     * 异常转换函数，将源异常转换为目标异常类型。
     */
    @NonNull
    protected final Function<? super E, ? extends T> throwingMapper;
    
    /**
     * 是否启用单例模式，true表示值仅获取一次并缓存。
     */
    protected final boolean singleton;
    
    /**
     * 关闭状态标志，用于跟踪资源是否已关闭。
     */
    private final AtomicBoolean closed = new AtomicBoolean();
    
    /**
     * 单例模式下的值缓存，使用volatile确保线程可见性。
     */
    private volatile Supplier<V> singletonSupplier;
    
    /**
     * 关闭操作执行器，用于执行额外的关闭逻辑。
     */
    @NonNull
    protected final ThrowingRunnable<? extends T> closeable;

    /**
     * 返回当前对象本身，用于支持链式调用。
     *
     * @return 当前Pipeline实例
     */
    @Override
    public Pipeline<V, T> closeable() {
        return this;
    }

    /**
     * 获取映射后的值，支持单例模式和非单例模式。
     * 在单例模式下，值会被缓存且仅获取一次；非单例模式下每次调用都会重新获取并映射值。
     *
     * @return 映射后的值
     * @throws T 可能抛出的目标异常类型
     */
    @Override
    public V get() throws T {
        if (singleton) {
            if (singletonSupplier == null) {
                synchronized (this) {
                    if (singletonSupplier == null) {
                        try {
                            V value = run(source);
                            singletonSupplier = () -> value;
                        } finally {
                            closed.set(true);
                        }
                    }
                }
            }
            return singletonSupplier.get();
        }
        return run(this.source);
    }

    /**
     * 执行值获取和映射的核心逻辑，包括异常处理和资源清理。
     * 该方法会调用源供应者获取值，应用映射函数，最后执行资源清理操作。
     *
     * @param supplier 源供应者
     * @return 映射后的值
     * @throws T 可能抛出的目标异常类型
     */
    @SuppressWarnings("unchecked")
    public V run(ThrowingSupplier<? extends S, ? extends E> supplier) throws T {
        try {
            S source = supplier.get();
            try {
                return mapper.apply(source);
            } finally {
                endpoint.accept(source);
            }
        } catch (Throwable e) {
            throw throwingMapper.apply((E) e);
        }
    }

    /**
     * 关闭资源，执行注册的关闭操作。
     *
     * @throws T 可能抛出的目标异常类型
     */
    @Override
    public void close() throws T {
        closeable.run();
    }

    /**
     * 检查资源是否已关闭。
     * 在单例模式下，资源在首次获取后即被标记为关闭；非单例模式下始终返回false。
     *
     * @return 资源是否已关闭
     */
    @Override
    public boolean isClosed() {
        return singleton && closed.get();
    }

    /**
     * 添加额外的映射转换，返回新的MappingThrowingSupplier实例。
     * 新实例会先应用当前映射函数，再应用指定的映射函数。
     *
     * @param <R>    新的目标值类型
     * @param mapper 额外的映射函数
     * @return 新的MappingThrowingSupplier实例
     */
    @Override
    public <R> MappingThrowingSupplier<S, R, E, T, W> map(@NonNull ThrowingFunction<? super V, ? extends R, T> mapper) {
        return new MappingThrowingSupplier<>(this.source, this.mapper.andThen(mapper), endpoint, throwingMapper,
                singleton, closeable);
    }
}