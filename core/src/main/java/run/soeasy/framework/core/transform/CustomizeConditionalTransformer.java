package run.soeasy.framework.core.transform;

import java.util.Collections;
import java.util.Set;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.TypeMapping;

/**
 * 自定义条件转换器，实现{@link ConditionalTransformer}接口，
 * 通过组合现有转换器和类型映射集合，提供条件转换能力。
 * <p>
 * 该转换器将类型映射判断和实际转换逻辑分离，
 * 在执行转换前会同时检查类型映射和内部转换器的可行性，
 * 适用于需要自定义转换规则的场景。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>条件判断：基于类型映射集合和内部转换器双重验证转换可行性</li>
 *   <li>组合模式：通过组合现有转换器实现转换逻辑，提高复用性</li>
 *   <li>灵活配置：支持单类型映射或多类型映射配置</li>
 * </ul>
 *
 * @author soeasy.run
 * @see ConditionalTransformer
 * @see TypeMapping
 * @see Transformer
 */
@Getter
@RequiredArgsConstructor
public class CustomizeConditionalTransformer implements ConditionalTransformer {
    
    /** 支持的类型映射集合，不可为null */
    @NonNull
    private final Set<TypeMapping> transformableTypeMappings;
    
    /** 实际执行转换的转换器，不可为null */
    @NonNull
    private final Transformer transformer;

    /**
     * 构造单类型映射的条件转换器
     * 
     * @param typeMapping 支持的类型映射
     * @param transformer 实际执行转换的转换器
     */
    public CustomizeConditionalTransformer(TypeMapping typeMapping, Transformer transformer) {
        this(Collections.singleton(typeMapping), transformer);
    }

    /**
     * 判断是否支持从源类型到目标类型的转换
     * <p>
     * 同时满足以下条件时返回true：
     * <ol>
     *   <li>类型映射集合中存在匹配的映射</li>
     *   <li>内部转换器支持该类型转换</li>
     * </ol>
     * 
     * @param sourceTypeDescriptor 源类型描述符，不可为null
     * @param targetTypeDescriptor 目标类型描述符，不可为null
     * @return 若支持转换返回true，否则false
     * @throws NullPointerException 若transformer为null
     */
    @Override
    public boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor,
            @NonNull TypeDescriptor targetTypeDescriptor) {
        return ConditionalTransformer.super.canTransform(sourceTypeDescriptor, targetTypeDescriptor)
                && transformer.canTransform(sourceTypeDescriptor, targetTypeDescriptor);
    }

    /**
     * 执行从源对象到目标对象的属性转换
     * <p>
     * 委托给内部转换器执行实际转换，不做额外处理
     * 
     * @param source 源对象，不可为null
     * @param sourceTypeDescriptor 源类型描述符，不可为null
     * @param target 目标对象，不可为null
     * @param targetTypeDescriptor 目标类型描述符，不可为null
     * @return 转换是否成功
     * @throws ConversionException 转换过程中抛出的异常
     * @throws NullPointerException 若transformer为null
     */
    @Override
    public boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
            @NonNull Object target, @NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
        return transformer.transform(source, sourceTypeDescriptor, target, targetTypeDescriptor);
    }
}