package run.soeasy.framework.core.transform;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;

/**
 * 抽象条件转换器基类，继承自{@link AbstractTransformer}并实现{@link ConditionalTransformer}接口，
 * 为条件转换器提供基于类型映射的条件判断默认实现。
 * <p>
 * 该类重写了{@link #canTransform}方法，将条件判断逻辑委托给{@link ConditionalTransformer}接口的默认实现，
 * 子类可通过重写此方法或提供类型映射集合来定义具体的转换条件。
 * </p>
 *
 * <p><b>实现细节：</b>
 * <ul>
 *   <li>继承自{@link AbstractTransformer}，支持转换器注入和基本转换功能</li>
 *   <li>实现{@link ConditionalTransformer}，提供基于类型映射的条件转换能力</li>
 *   <li>重写{@link #canTransform}方法，调用接口默认实现</li>
 * </ul>
 * </p>
 *
 * <p><b>潜在问题：</b>
 * <ul>
 *   <li>未实现{@link ConditionalTransformer#getTransformableTypeMappings}抽象方法，子类必须实现</li>
 *   <li>{@link #canTransform}方法仅调用父接口默认实现，需结合{@link #getTransformableTypeMappings}使用</li>
 *   <li>未处理类型映射集合的线程安全问题，多线程环境需自行保证</li>
 * </ul>
 * </p>
 *
 * @author soeasy.run
 * @see AbstractTransformer
 * @see ConditionalTransformer
 */
public abstract class AbstractConditionalTransformer extends AbstractTransformer implements ConditionalTransformer {

    /**
     * 判断是否支持从源类型到目标类型的转换（条件转换实现）
     * <p>
     * 该实现直接调用{@link ConditionalTransformer}接口的默认实现，
     * 基于{@link #getTransformableTypeMappings}返回的类型映射集合进行判断。
     * 子类需实现{@link ConditionalTransformer#getTransformableTypeMappings}方法
     * 以提供具体的可转换类型映射。
     * 
     * @param sourceTypeDescriptor 源类型描述符，不可为null
     * @param targetTypeDescriptor 目标类型描述符，不可为null
     * @return 若存在匹配的类型映射返回true，否则false
     */
    @Override
    public boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor,
            @NonNull TypeDescriptor targetTypeDescriptor) {
        return ConditionalTransformer.super.canTransform(sourceTypeDescriptor, targetTypeDescriptor);
    }
}