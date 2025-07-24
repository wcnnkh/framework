package run.soeasy.framework.core.convert;

import java.util.Collections;
import java.util.Set;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 自定义条件转换器，允许通过类型映射集合和基础转换器组合实现灵活的条件转换逻辑。
 * 该转换器实现了{@link ConditionalConverter}接口，支持基于类型映射和基础转换器的条件转换检查。
 *
 * <p>核心特性：
 * <ul>
 *   <li>灵活的类型映射：支持指定多个可转换的类型映射</li>
 *   <li>条件转换检查：结合类型映射和基础转换器的条件判断</li>
 *   <li>转换器组合：封装基础转换器以复用转换逻辑</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * // 创建支持String到Integer的自定义条件转换器
 * CustomizeConditionalConverter converter = new CustomizeConditionalConverter(
 *     new TypeMapping(String.class, Integer.class),
 *     source -> Integer.valueOf((String) source)
 * );
 * }</pre>
 *
 * @author soeasy.run
 * @see ConditionalConverter
 * @see TypeMapping
 * @see Converter
 */
@Getter
@RequiredArgsConstructor
public class CustomizeConditionalConverter implements ConditionalConverter {
    /** 可转换的类型映射集合，不可为null */
    @NonNull
    private final Set<TypeMapping> convertibleTypeMappings;
    
    /** 实际执行转换的基础转换器，不可为null */
    @NonNull
    private final Converter converter;

    /**
     * 创建支持单个类型映射的自定义条件转换器
     * 
     * @param typeMapping 可转换的类型映射
     * @param converter 实际执行转换的基础转换器
     * @throws NullPointerException 若typeMapping或converter为null
     */
    public CustomizeConditionalConverter(TypeMapping typeMapping, Converter converter) {
        this(Collections.singleton(typeMapping), converter);
    }

    /**
     * 检查是否支持从源类型到目标类型的转换
     * <p>
     * 转换支持条件：
     * <ol>
     *   <li>源类型和目标类型匹配任一可转换类型映射</li>
     *   <li>基础转换器支持该转换</li>
     * </ol>
     * 
     * @param sourceTypeDescriptor 源类型描述符，不可为null
     * @param targetTypeDescriptor 目标类型描述符，不可为null
     * @return 若支持转换返回true，否则返回false
     */
    @Override
    public boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
            @NonNull TypeDescriptor targetTypeDescriptor) {
        // 先检查类型映射是否匹配，再检查基础转换器是否支持
        return ConditionalConverter.super.canConvert(sourceTypeDescriptor, targetTypeDescriptor)
                && converter.canConvert(sourceTypeDescriptor, targetTypeDescriptor);
    }

    /**
     * 执行类型转换操作
     * 
     * @param source 源对象，可为null
     * @param sourceTypeDescriptor 源类型描述符，不可为null
     * @param targetTypeDescriptor 目标类型描述符，不可为null
     * @return 转换后的目标对象
     * @throws ConversionException 转换失败时抛出
     */
    @Override
    public Object convert(Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
            @NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
        return converter.convert(source, sourceTypeDescriptor, targetTypeDescriptor);
    }
}