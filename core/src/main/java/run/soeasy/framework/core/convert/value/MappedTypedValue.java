package run.soeasy.framework.core.convert.value;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.support.SystemConversionService;

/**
 * 映射类型化值包装器，用于将源类型化值转换为目标类型的包装实现。
 * <p>
 * 该类实现了{@link TypedValueWrapper}接口，通过指定的转换器将源类型化值转换为目标类型，
 * 支持类型安全的转换操作，并提供了默认的系统转换服务作为后备转换方案。
 *
 * <p><b>核心功能：</b>
 * <ul>
 *   <li>类型映射转换：使用指定转换器将源值转换为目标类型</li>
 *   <li>后备转换机制：当指定转换器不支持时，使用系统转换服务</li>
 *   <li>透明包装：实现{@link TypedValueWrapper}接口，保持接口透明性</li>
 * </ul>
 *
 * @param <W> 被包装的源类型化值类型，需实现{@link TypedValue}
 * 
 * @author soeasy.run
 * @see TypedValueWrapper
 * @see Converter
 * @see SystemConversionService
 */
@Getter
@RequiredArgsConstructor
class MappedTypedValue<W extends TypedValue> implements TypedValueWrapper<W> {
    
    /** 被包装的源类型化值实例 */
    private final W source;
    
    /** 目标类型描述符 */
    private final TypeDescriptor typeDescriptor;
    
    /** 用于类型转换的转换器 */
    private final Converter converter;

    /**
     * 获取转换后的类型描述符
     * <p>
     * 直接返回构造时指定的目标类型描述符，
     * 表示当前包装器转换后的目标类型。
     * 
     * @return 目标类型描述符
     */
    @Override
    public TypeDescriptor getReturnTypeDescriptor() {
        return typeDescriptor;
    }

    /**
     * 执行类型转换并获取转换后的值
     * <p>
     * 转换逻辑：
     * <ol>
     *   <li>使用源类型化值的类型描述符和目标类型描述符检查转换器是否支持</li>
     *   <li>若支持，使用指定转换器执行转换</li>
     *   <li>若不支持，使用系统转换服务{@link SystemConversionService}执行转换</li>
     * </ol>
     * 
     * @return 转换后的值
     * @throws ConversionException 转换过程中可能抛出的异常（未显式捕获）
     */
    @Override
    public Object get() {
        TypeDescriptor sourceTypeDescriptor = source.getReturnTypeDescriptor();
        if (converter.canConvert(sourceTypeDescriptor, typeDescriptor)) {
            return converter.convert(source.get(), sourceTypeDescriptor, typeDescriptor);
        }
        return SystemConversionService.getInstance().convert(source.get(), sourceTypeDescriptor, typeDescriptor);
    }
}