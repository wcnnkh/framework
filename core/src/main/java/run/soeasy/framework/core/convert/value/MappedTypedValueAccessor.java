package run.soeasy.framework.core.convert.value;

import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.TypeDescriptor;

/**
 * 映射类型化值访问器，用于将源类型化值访问器转换为目标类型的包装实现。
 * <p>
 * 该类继承自{@link MappedTypedValue}并实现{@link TypedValueAccessorWrapper}接口，
 * 支持通过指定转换器将源类型化值访问器的返回值转换为目标类型，
 * 同时保留了对值的读写访问能力，适用于需要类型转换的数据访问场景。
 *
 * <p><b>核心功能：</b>
 * <ul>
 *   <li>类型安全的转换：基于源类型和目标类型描述符执行类型转换</li>
 *   <li>双向数据访问：继承父类的读取能力，同时实现写入接口</li>
 *   <li>透明包装：保持与源访问器一致的接口，对调用者透明</li>
 * </ul>
 *
 * @param <W> 被包装的源类型化值访问器类型，需实现{@link TypedValueAccessor}
 * 
 * @author soeasy.run
 * @see MappedTypedValue
 * @see TypedValueAccessorWrapper
 */
final class MappedTypedValueAccessor<W extends TypedValueAccessor> extends MappedTypedValue<W>
        implements TypedValueAccessorWrapper<W> {
    
    /**
     * 构造映射类型化值访问器实例
     * 
     * @param source 被包装的源类型化值访问器
     * @param typeDescriptor 目标类型描述符
     * @param converter 用于类型转换的转换器
     */
    public MappedTypedValueAccessor(W source, TypeDescriptor typeDescriptor, Converter converter) {
        super(source, typeDescriptor, converter);
    }
}