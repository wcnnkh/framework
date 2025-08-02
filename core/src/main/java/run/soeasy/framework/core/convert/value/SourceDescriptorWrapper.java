package run.soeasy.framework.core.convert.value;

import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.domain.Wrapper;

/**
 * 源数据描述符包装器接口，用于装饰{@link SourceDescriptor}实现，支持透明代理和功能增强。
 * <p>
 * 该函数式接口继承自{@link SourceDescriptor}和{@link Wrapper}，
 * 允许通过包装现有源描述符实例来添加额外逻辑，同时保持接口的透明性，
 * 适用于需要对源类型描述进行增强而不修改原始实现的场景。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>透明代理：默认实现将所有方法调用转发给被包装的源描述符</li>
 *   <li>函数式支持：作为函数式接口，可通过lambda表达式快速创建轻量级包装器</li>
 *   <li>类型安全：泛型参数确保包装器与被包装对象的类型一致性</li>
 *   <li>扩展灵活：支持通过继承或组合方式添加自定义增强逻辑</li>
 * </ul>
 *
 * @param <W> 被包装的源描述符类型，需实现{@link SourceDescriptor}
 * 
 * @author soeasy.run
 * @see SourceDescriptor
 * @see Wrapper
 * @see run.soeasy.framework.core.convert.TypeDescriptor
 */
@FunctionalInterface
public interface SourceDescriptorWrapper<W extends SourceDescriptor> extends SourceDescriptor, Wrapper<W> {

    /**
     * 获取被包装源描述符的返回类型描述符（透明代理实现）
     * <p>
     * 该默认实现将调用转发给被包装的源描述符实例，
     * 子类可重写此方法以添加自定义逻辑（如类型转换、日志记录等）。
     * 
     * @return 被包装源描述符的返回类型描述符
     * @see Wrapper#getSource()
     */
    @Override
    default TypeDescriptor getReturnTypeDescriptor() {
        return getSource().getReturnTypeDescriptor();
    }
}