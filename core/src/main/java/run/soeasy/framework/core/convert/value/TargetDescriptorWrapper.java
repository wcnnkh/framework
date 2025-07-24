package run.soeasy.framework.core.convert.value;

import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.domain.Wrapper;

/**
 * 目标类型描述符包装器接口，用于装饰{@link TargetDescriptor}实现，支持透明代理和功能增强。
 * <p>
 * 该接口继承自{@link TargetDescriptor}和{@link Wrapper}，允许通过包装现有目标描述符实例
 * 来添加额外逻辑（如日志记录、权限校验、类型转换增强等），同时保持接口的透明性，
 * 适用于需要对目标类型描述进行非侵入式增强的场景。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>透明代理：默认实现将所有方法调用转发给被包装的目标描述符</li>
 *   <li>装饰器模式：支持在不修改原始类的情况下添加新功能</li>
 *   <li>类型安全：泛型参数确保包装器与被包装对象的类型一致性</li>
 *   <li>函数式扩展：可通过lambda表达式快速创建轻量级包装器</li>
 * </ul>
 *
 * @param <W> 被包装的目标描述符类型，需实现{@link TargetDescriptor}
 * 
 * @author soeasy.run
 * @see TargetDescriptor
 * @see Wrapper
 * @see run.soeasy.framework.core.convert.TypeDescriptor
 */
public interface TargetDescriptorWrapper<W extends TargetDescriptor> extends TargetDescriptor, Wrapper<W> {

    /**
     * 获取被包装目标描述符的类型描述符（透明代理实现）
     * <p>
     * 该默认实现将调用转发给被包装的目标描述符实例，
     * 子类可重写此方法以添加自定义逻辑（如类型转换、日志记录等）。
     * 
     * @return 被包装目标描述符的类型描述符
     * @see Wrapper#getSource()
     */
    @Override
    default TypeDescriptor getRequiredTypeDescriptor() {
        return getSource().getRequiredTypeDescriptor();
    }

    /**
     * 获取被包装目标描述符的非空约束（透明代理实现）
     * <p>
     * 该默认实现将调用转发给被包装的目标描述符实例，
     * 子类可重写此方法以修改空值约束逻辑（如基于注解动态判断）。
     * 
     * @return 被包装目标描述符的非空约束状态
     * @see TargetDescriptor#isRequired()
     */
    @Override
    default boolean isRequired() {
        return getSource().isRequired();
    }
}