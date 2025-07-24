package run.soeasy.framework.core.convert.value;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.TypeDescriptor;

/**
 * 自定义类型化值访问器，继承自{@link CustomizeTypedDataAccessor}并实现{@link TypedValueAccessor}接口，
 * 提供类型安全的值访问和转换能力。该类在获取值时会使用指定的转换器进行类型转换，
 * 支持处理{@link TypedData}类型的值或普通对象。
 * 
 * <p><b>核心功能：</b>
 * <ul>
 *   <li>类型转换：通过{@link Converter}实现值的类型转换</li>
 *   <li>自动类型推断：对于非{@link TypedData}的值，自动推断源类型</li>
 *   <li>链式访问：实现{@link TypedValue#value()}方法，支持链式操作</li>
 * </ul>
 * 
 * <p><b>潜在问题：</b>
 * <ul>
 *   <li>线程安全：非线程安全实现，多线程环境下可能出现数据竞争</li>
 *   <li>类型转换异常：转换过程中可能抛出{@link run.soeasy.framework.core.convert.ConversionException}，未被捕获</li>
 *   <li>递归转换风险：若值本身是{@link TypedValueAccessor}，可能导致递归转换</li>
 *   <li>空值处理：当值为null时，类型推断可能失败</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see TypedValueAccessor
 * @see Converter
 * @see CustomizeTypedDataAccessor
 */
@Getter
@Setter
public class CustomizeTypedValueAccessor extends CustomizeTypedDataAccessor<Object> implements TypedValueAccessor {
    
    /** 用于类型转换的转换器，默认使用可赋值转换器 */
    @NonNull
    private Converter converter = Converter.assignable();

    /**
     * 获取转换后的值，自动处理{@link TypedData}类型或普通对象
     * <p>
     * 转换逻辑：
     * <ol>
     *   <li>若值为{@link TypedData}，获取其值和类型描述符</li>
     *   <li>否则将值作为源，自动推断源类型</li>
     *   <li>使用{@link #converter}执行类型转换</li>
     * </ol>
     * 
     * @return 转换后的值
     * @throws run.soeasy.framework.core.convert.ConversionException 转换失败时抛出
     */
    @Override
    public Object get() {
        Object value = super.get();
        Object source;
        TypeDescriptor sourceTypeDescriptor;
        if (value instanceof TypedData) {
            TypedData<?> typedData = (TypedData<?>) value;
            source = typedData.get();
            sourceTypeDescriptor = typedData.getReturnTypeDescriptor();
        } else {
            source = value;
            sourceTypeDescriptor = TypeDescriptor.forObject(source);
        }
        return converter.convert(source, sourceTypeDescriptor, getTypeDescriptor());
    }

    /**
     * 返回当前实例作为{@link TypedValue}，支持链式操作
     * 
     * @return 当前类型化值访问器实例
     */
    @Override
    public TypedValue value() {
        return this;
    }
}