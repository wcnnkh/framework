package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.NonNull;

/**
 * 映射型流水线实现，支持将源流水线的资源进行类型转换，
 * 并提供增强的资源管理和异常处理能力。该类继承自{@link MappingThrowingSupplier}，
 * 在供应者功能基础上增加了流水线特有的资源关闭和状态检查功能。
 *
 * <p>核心特性：
 * <ul>
 *   <li>资源映射：通过{@link ThrowingFunction}将源资源类型S转换为目标类型V</li>
 *   <li>双重关闭：同时管理源流水线和当前实例的资源释放</li>
 *   <li>异常转换：使用{@link Function}将源异常E统一转换为目标异常T</li>
 *   <li>状态聚合：{@link #isClosed()}同时检查源流水线和当前实例的关闭状态</li>
 *   <li>单例模式：支持缓存资源值避免重复获取（通过singleton参数控制）</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>需要转换资源类型并管理生命周期的场景（如数据库连接类型转换）</li>
 *   <li>微服务间资源格式转换时的异常标准化处理</li>
 *   <li>组合多个流水线操作并统一关闭逻辑的场景</li>
 *   <li>需要缓存昂贵资源获取操作的场景</li>
 * </ul>
 *
 * @param <S> 源流水线提供的资源类型
 * @param <V> 映射后的目标资源类型
 * @param <E> 源流水线可能抛出的异常类型
 * @param <T> 最终抛出的目标异常类型
 * @param <W> 源流水线类型，需实现{@link Pipeline}接口
 * @see Pipeline
 * @see MappingThrowingSupplier
 */
class MappingPipeline<S, V, E extends Throwable, T extends Throwable, W extends Pipeline<? extends S, ? extends E>>
        extends MappingThrowingSupplier<S, V, E, T, W> {

    /**
     * 构造映射型流水线实例。
     *
     * @param source         源流水线，不可为null
     * @param mapper         类型映射函数，将S转换为V，不可为null
     * @param endpoint       资源清理消费者，资源使用后执行，可为null
     * @param throwingMapper 异常转换函数，不可为null
     * @param singleton      是否启用单例模式（缓存资源值）
     * @param closeable      额外关闭操作执行器，不可为null
     */
    public MappingPipeline(@NonNull W source, 
                          @NonNull ThrowingFunction<? super S, ? extends V, T> mapper,
                          ThrowingConsumer<? super S, ? extends E> endpoint,
                          @NonNull Function<? super E, ? extends T> throwingMapper,
                          boolean singleton, 
                          @NonNull ThrowingRunnable<? extends T> closeable) {
        super(source, mapper, endpoint, throwingMapper, singleton, closeable);
    }

    /**
     * 检查流水线是否已关闭，同时验证源流水线和当前实例的关闭状态。
     * 只要其中一个已关闭，即返回true。
     *
     * @return true如果源流水线或当前实例已关闭，否则false
     */
    @Override
    public boolean isClosed() {
        return super.isClosed() || source.isClosed();
    }

    /**
     * 关闭流水线资源，先关闭源流水线再关闭当前实例。
     * 该方法会捕获源流水线关闭时的异常并转换为目标异常类型。
     *
     * @throws T 转换后的目标异常类型
     */
    @SuppressWarnings("unchecked")
    @Override
    public void close() throws T {
        try {
            source.close();
        } catch (Throwable e) {
            throw throwingMapper.apply((E) e);
        } finally {
            super.close();
        }
    }
}