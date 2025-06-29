package run.soeasy.framework.core.transform;

import java.util.Set;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.TypeMapping;

/**
 * ConditionalTransformer 接口扩展了 Transformer 接口，支持基于类型映射的条件转换。
 * 实现类需提供可转换的类型映射集合，框架将根据这些映射判断是否支持特定转换。
 * 
 * <p>该接口通过 {@link TypeMapping} 集合定义可转换的类型对，
 * 并在 {@link #canTransform(TypeDescriptor, TypeDescriptor)} 方法中使用这些映射进行判断。
 * 
 * @see Transformer
 * @see TypeMapping
 * @see TypeDescriptor
 */
public interface ConditionalTransformer extends Transformer {
    /**
     * 获取当前转换器支持的所有类型映射。
     * 每个 TypeMapping 定义了一组可转换的源类型和目标类型。
     * 
     * @return 不可变的 TypeMapping 集合，不会返回 null
     */
    Set<TypeMapping> getTransformableTypeMappings();

    /**
     * 判断当前转换器是否支持从源类型到目标类型的转换。
     * 此实现遍历 {@link #getTransformableTypeMappings()} 返回的所有类型映射，
     * 并检查是否存在与给定源类型和目标类型匹配的映射。
     * 
     * @param sourceTypeDescriptor 源类型描述符，不可为 null
     * @param targetTypeDescriptor 目标类型描述符，不可为 null
     * @return 如果存在匹配的类型映射返回 true，否则返回 false
     */
    @Override
    default boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor,
            @NonNull TypeDescriptor targetTypeDescriptor) {
        return getTransformableTypeMappings().stream()
                .anyMatch((mapping) -> mapping.canConvert(sourceTypeDescriptor, targetTypeDescriptor));
    }
}