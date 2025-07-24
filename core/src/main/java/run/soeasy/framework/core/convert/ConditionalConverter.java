package run.soeasy.framework.core.convert;

import java.util.Set;

import lombok.NonNull;

/**
 * 条件型转换器接口，支持基于类型映射集合的条件转换。
 * 该接口扩展自{@link Converter}，通过定义一组可转换的类型映射，
 * 实现细粒度的类型转换控制，适用于需要复杂类型匹配逻辑的场景。
 *
 * <p>核心特性：
 * <ul>
 *   <li>类型映射集合：通过{@link TypeMapping}定义可转换的源类型与目标类型组合</li>
 *   <li>条件判断：默认实现基于类型映射集合判断是否可转换</li>
 *   <li>可组合性：支持多个条件型转换器的组合使用</li>
 * </ul>
 *
 * <p>典型应用场景：
 * <pre>
 * public class CustomConverter implements ConditionalConverter {
 *     private final Set&lt;TypeMapping&gt; mappings = Set.of(
 *         new SimpleTypeMapping(String.class, Integer.class),
 *         new SimpleTypeMapping(String.class, Long.class)
 *     );
 *     
 *     &#64;Override
 *     public Set&lt;TypeMapping&gt; getConvertibleTypeMappings() {
 *         return mappings;
 *     }
 *     
 *     &#64;Override
 *     public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
 *         // 实现转换逻辑
 *     }
 * }
 * </pre>
 *
 * @author soeasy.run
 * @see Converter
 * @see TypeMapping
 * @see TypeDescriptor
 */
public interface ConditionalConverter extends Converter {

    /**
     * 获取可转换的类型映射集合
     * 
     * @return 类型映射集合，不可为null
     */
    Set<TypeMapping> getConvertibleTypeMappings();

    /**
     * 判断源类型是否可转换为目标类型
     * 默认实现基于类型映射集合进行判断
     * 
     * @param sourceTypeDescriptor 源类型描述符，不可为null
     * @param targetTypeDescriptor 目标类型描述符，不可为null
     * @return true表示可转换，false表示不可转换
     */
    @Override
    default boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
            @NonNull TypeDescriptor targetTypeDescriptor) {
        return getConvertibleTypeMappings().stream()
                .anyMatch((e) -> e.canConvert(sourceTypeDescriptor, targetTypeDescriptor));
    }
}