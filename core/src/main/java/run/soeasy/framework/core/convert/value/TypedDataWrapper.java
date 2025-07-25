package run.soeasy.framework.core.convert.value;

/**
 * 类型化数据包装器接口，用于装饰{@link TypedData}实现，支持透明代理和功能增强。
 * <p>
 * 该接口继承自{@link TypedData}和{@link SourceDescriptorWrapper}，
 * 允许通过包装现有类型化数据实例来添加额外逻辑，同时保持接口的透明性，
 * 适用于需要对类型化数据进行非侵入式增强的场景。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>透明代理：默认实现将所有方法调用转发给被包装的类型化数据</li>
 *   <li>功能增强：支持添加日志记录、缓存优化、类型转换增强等额外功能</li>
 *   <li>类型安全：泛型参数确保包装器与被包装对象的类型一致性</li>
 *   <li>函数式支持：作为函数式接口，可通过lambda表达式快速创建轻量级包装器</li>
 * </ul>
 *
 * @param <T> 数据的类型
 * @param <W> 被包装的类型化数据类型，需实现{@link TypedData}
 * 
 * @author soeasy.run
 * @see TypedData
 * @see SourceDescriptorWrapper
 */
@FunctionalInterface
public interface TypedDataWrapper<T, W extends TypedData<T>> extends TypedData<T>, SourceDescriptorWrapper<W> {

    /**
     * 将当前类型化数据转换为{@link TypedValue}实例（透明代理实现）
     * <p>
     * 该默认实现将调用转发给被包装的类型化数据实例，
     * 子类可重写此方法以添加自定义逻辑（如值转换、日志记录等）。
     * 
     * @return 被包装类型化数据的TypedValue实例
     */
    @Override
    default TypedValue value() {
        return getSource().value();
    }

    /**
     * 获取数据值（透明代理实现）
     * <p>
     * 该默认实现将调用转发给被包装的类型化数据实例，
     * 子类可重写此方法以添加自定义逻辑（如缓存优化、权限校验等）。
     * 
     * @return 被包装类型化数据的值
     */
    @Override
    default T get() {
        return getSource().get();
    }
}