package run.soeasy.framework.core.convert.value;

/**
 * 可访问描述符包装器接口，用于装饰{@link AccessibleDescriptor}实现，支持透明代理和功能增强。
 * <p>
 * 该函数式接口继承自{@link AccessibleDescriptor}、{@link SourceDescriptorWrapper}和{@link TargetDescriptorWrapper}，
 * 允许通过包装现有可访问描述符实例来添加额外逻辑，同时保持接口的透明性，
 * 适用于需要对访问描述符进行非侵入式增强的场景。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>透明代理：默认实现将所有方法调用转发给被包装的可访问描述符</li>
 *   <li>功能增强：支持添加日志记录、权限校验、访问控制等额外功能</li>
 *   <li>类型安全：泛型参数确保包装器与被包装对象的类型一致性</li>
 *   <li>函数式支持：作为函数式接口，可通过lambda表达式快速创建轻量级包装器</li>
 * </ul>
 *
 * @param <W> 被包装的可访问描述符类型，需实现{@link AccessibleDescriptor}
 * 
 * @author soeasy.run
 * @see AccessibleDescriptor
 * @see SourceDescriptorWrapper
 * @see TargetDescriptorWrapper
 */
@FunctionalInterface
public interface AccessibleDescriptorWrapper<W extends AccessibleDescriptor>
        extends AccessibleDescriptor, SourceDescriptorWrapper<W>, TargetDescriptorWrapper<W> {

    /**
     * 判断数据位置是否可读（透明代理实现）
     * <p>
     * 该默认实现将调用转发给被包装的可访问描述符实例，
     * 子类可重写此方法以添加自定义逻辑（如权限校验、访问控制等）。
     * 
     * @return 被包装可访问描述符的可读状态
     */
    @Override
    default boolean isReadable() {
        return getSource().isReadable();
    }

    /**
     * 判断数据位置是否可写（透明代理实现）
     * <p>
     * 该默认实现将调用转发给被包装的可访问描述符实例，
     * 子类可重写此方法以添加自定义逻辑（如权限校验、访问控制等）。
     * 
     * @return 被包装可访问描述符的可写状态
     */
    @Override
    default boolean isWriteable() {
        return getSource().isWriteable();
    }
}