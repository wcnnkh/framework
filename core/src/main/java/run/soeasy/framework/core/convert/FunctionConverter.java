package run.soeasy.framework.core.convert;

import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

import lombok.NonNull;

/**
 * 基于Function的类型转换器实现，支持将源类型转换为目标类型。
 * 该转换器使用提供的Function进行类型转换，并通过TypeMapping定义可转换的类型对。
 *
 * <p>核心特性：
 * <ul>
 *   <li>类型安全转换：通过泛型和Class参数确保类型一致性</li>
 *   <li>函数式转换：使用Function接口定义转换逻辑</li>
 *   <li>条件转换支持：实现ConditionalConverter接口，支持条件转换检查</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * ConverterRegistry registry = ...;
 * FunctionConverter<String, Integer> converter =
 *     new FunctionConverter<>(String.class, Integer.class, Integer::valueOf);
 * registry.addConverter(converter);
 * }</pre>
 *
 * @param <S> 源类型
 * @param <T> 目标类型
 * 
 * @author soeasy.run
 * @see ConditionalConverter
 * @see TypeMapping
 * @see Function
 */
public class FunctionConverter<S, T> implements ConditionalConverter {
    /** 可转换的类型映射 */
    private final TypeMapping typeMapping;
    
    /** 执行转换的函数 */
    private final Function<? super S, ? extends T> function;

    /**
     * 创建一个新的FunctionConverter实例。
     * 
     * @param sourceType 源类型的Class对象，不可为null
     * @param targetType 目标类型的Class对象，不可为null
     * @param function 执行转换的函数，不可为null
     * @throws NullPointerException 如果任何参数为null
     */
    public FunctionConverter(@NonNull Class<S> sourceType, @NonNull Class<T> targetType,
            @NonNull Function<? super S, ? extends T> function) {
        this.typeMapping = new TypeMapping(sourceType, targetType);
        this.function = function;
    }

    /**
     * 获取此转换器支持的类型映射集合。
     * 
     * @return 包含单个类型映射的不可变集合
     */
    @Override
    public Set<TypeMapping> getConvertibleTypeMappings() {
        return Collections.singleton(typeMapping);
    }

    /**
     * 将源对象转换为目标类型。
     * 
     * @param source 源对象，可为null
     * @param sourceTypeDescriptor 源类型描述符，不可为null
     * @param targetTypeDescriptor 目标类型描述符，不可为null
     * @return 转换后的目标对象
     * @throws ConversionException 如果转换失败
     * @throws ClassCastException 如果源对象类型与预期不符
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object convert(Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
            @NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
        try {
            // 将源对象强制转换为泛型类型S，并应用转换函数
            return function.apply((S) source);
        } catch (ClassCastException ex) {
            // 源对象类型与预期不符
            throw new ConversionException("Failed to convert object of type '" + 
                    (source != null ? source.getClass().getName() : "null") + 
                    "' to type '" + targetTypeDescriptor.getName() + "'", ex);
        } catch (Exception ex) {
            // 转换函数执行过程中发生异常
            throw new ConversionException("Conversion failed using function converter", ex);
        }
    }
}