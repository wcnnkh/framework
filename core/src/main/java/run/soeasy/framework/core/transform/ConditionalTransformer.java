package run.soeasy.framework.core.transform;

import java.util.Set;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.TypeMapping;

/**
 * 条件转换器接口，扩展自{@link Transformer}，支持基于类型映射的条件转换判断。
 * <p>
 * 该接口通过维护一组{@link TypeMapping}定义可转换的类型对，
 * 在{@link #canTransform(TypeDescriptor, TypeDescriptor)}方法中基于类型映射集合
 * 判断是否支持特定的源类型到目标类型的转换，适用于需要细粒度控制转换规则的场景。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>类型映射集合：通过{@link #getTransformableTypeMappings()}管理可转换的类型对</li>
 *   <li>条件转换判断：基于类型映射集合实现{@link Transformer#canTransform}逻辑</li>
 *   <li>默认实现：提供基于类型映射遍历的转换可行性判断</li>
 * </ul>
 * </p>
 *
 * <p><b>潜在问题：</b>
 * <ul>
 *   <li>性能损耗：默认实现使用流式遍历所有类型映射，大数据量时可能影响性能</li>
 *   <li>线程安全：未定义类型映射集合的线程安全策略，多线程环境需自行保证</li>
 *   <li>映射顺序：类型映射的顺序可能影响判断结果，需注意定义顺序</li>
 *   <li>泛型匹配：类型映射的泛型参数匹配逻辑未明确，可能存在类型擦除风险</li>
 * </ul>
 * </p>
 *
 * @author soeasy.run
 * @see Transformer
 * @see TypeMapping
 * @see TypeDescriptor
 */
public interface ConditionalTransformer extends Transformer {

    /**
     * 获取当前转换器支持的所有类型映射集合。
     * <p>
     * 每个{@link TypeMapping}定义了一组源类型到目标类型的可转换规则，
     * 实现类应确保返回的集合为不可变集合，避免外部修改导致转换规则失效。
     * 
     * @return 不可变的类型映射集合，不会返回null
     */
    Set<TypeMapping> getTransformableTypeMappings();

    /**
     * 判断是否支持从源类型到目标类型的转换（条件转换实现）。
     * <p>
     * 默认实现遍历{@link #getTransformableTypeMappings()}返回的所有类型映射，
     * 检查是否存在任意类型映射支持当前源类型和目标类型的转换。
     * 实现类可重写此方法以优化匹配逻辑（如添加缓存、优先级判断等）。
     * 
     * @param sourceTypeDescriptor 源类型描述符，不可为null
     * @param targetTypeDescriptor 目标类型描述符，不可为null
     * @return 存在匹配类型映射时返回true，否则返回false
     * @throws NullPointerException 若参数为null
     */
    @Override
    default boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor,
            @NonNull TypeDescriptor targetTypeDescriptor) {
        return getTransformableTypeMappings().stream()
                .anyMatch((mapping) -> mapping.canConvert(sourceTypeDescriptor, targetTypeDescriptor));
    }
}