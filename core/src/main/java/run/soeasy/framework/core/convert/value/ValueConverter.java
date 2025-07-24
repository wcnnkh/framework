package run.soeasy.framework.core.convert.value;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import run.soeasy.framework.core.convert.AbstractConditionalConverter;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ConverterNotFoundException;
import run.soeasy.framework.core.convert.ReversibleConverter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.domain.Value;
import run.soeasy.framework.core.domain.Version;

/**
 * 值转换器，支持对象与{@link Value}之间的双向转换。
 * <p>
 * 该转换器提供了常见类型的默认转换实现，包括基本类型、大数类型、枚举类型等。
 * 通过静态类型映射表，实现了高效的类型转换，并支持自定义转换器注册。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>双向转换：实现{@link ReversibleConverter}接口，支持对象与Value的互转</li>
 *   <li>静态映射表：预定义常见类型的转换函数，提升转换效率</li>
 *   <li>类型安全：通过{@link TypeDescriptor}确保类型转换的安全性</li>
 *   <li>可扩展性：支持通过静态方法注册自定义类型转换器</li>
 * </ul>
 * </p>
 *
 * <p><b>使用示例：</b>
 * <pre>{@code
 * // 将对象转换为Value
 * Value value = ValueConverter.DEFAULT.to("123", TypeDescriptor.forObject("123"), TypeDescriptor.valueOf(Integer.class));
 *
 * // 将Value转换为Integer
 * Integer intValue = (Integer) ValueConverter.DEFAULT.from(value, 
 *     TypeDescriptor.valueOf(Value.class), 
 *     TypeDescriptor.valueOf(Integer.class));
 * }</pre>
 * </p>
 *
 * @author soeasy.run
 * @see ReversibleConverter
 * @see Value
 * @see TypeDescriptor
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ValueConverter extends AbstractConditionalConverter implements ReversibleConverter<Object, Value> {
    /**
     * 默认的ValueConverter实例，推荐在大多数场景下使用。
     */
    public static final ValueConverter DEFAULT = new ValueConverter();
    
    /**
     * 类型到转换函数的映射表，存储常见类型的转换逻辑。
     * <p>
     * 注意：该映射表使用{@link HashMap}实现，不保证线程安全。
     * 在静态初始化块中完成所有初始化，避免运行时并发修改。
     * </p>
     */
    private static Map<Class<?>, BiFunction<? super Value, ? super TypeDescriptor, ? extends Object>> typeMap = new HashMap<>();

    static {
        // 初始化常见类型的转换函数
        typeMap.put(String.class, (a, b) -> a.getAsString());
        typeMap.put(BigInteger.class, (a, b) -> a.getAsBigInteger());
        typeMap.put(BigDecimal.class, (a, b) -> a.getAsBigDecimal());
        typeMap.put(Boolean.class, (a, b) -> a.getAsBoolean());
        typeMap.put(Character.class, (a, b) -> a.getAsChar());
        typeMap.put(Enum.class, (a, b) -> a.getAsEnum((Class<Enum>)b.getType()));
        typeMap.put(Version.class, (a, b) -> a.getAsVersion());
        typeMap.put(Number.class, (a, b) -> a.getAsNumber());
        typeMap.put(Byte.class, (a, b) -> a.getAsByte());
        typeMap.put(Short.class, (a, b) -> a.getAsShort());
        typeMap.put(Integer.class, (a, b) -> a.getAsInt());
        typeMap.put(Long.class, (a, b) -> a.getAsLong());
        typeMap.put(Float.class, (a, b) -> a.getAsFloat());
        typeMap.put(Double.class, (a, b) -> a.getAsDouble());
    }

    /**
     * 检查指定类型是否支持转换。
     * <p>
     * 如果类型存在于内部类型映射表中，则返回true，否则返回false。
     * </p>
     *
     * @param type 待检查的类型
     * @return 如果支持该类型的转换，返回true；否则返回false
     */
    public static boolean isValueType(Class<?> type) {
        return typeMap.containsKey(type);
    }

    /**
     * 将源对象转换为{@link Value}类型。
     * <p>
     * 如果源对象已经是Value类型，则直接返回；
     * 否则创建一个新的{@link CustomizeTypedValueAccessor}实例，
     * 设置源对象的值和类型描述符，并应用目标类型描述符进行映射转换。
     * </p>
     *
     * @param source 源对象
     * @param sourceTypeDescriptor 源对象的类型描述符
     * @param targetTypeDescriptor 目标类型描述符
     * @return 转换后的Value对象
     * @throws ConversionException 如果转换过程中发生错误
     */
    @Override
    public Value to(Object source, TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor)
            throws ConversionException {
        if(source instanceof Value) {
            return (Value) source;
        }
        
        CustomizeTypedValueAccessor accessor = new CustomizeTypedValueAccessor();
        accessor.setTypeDescriptor(sourceTypeDescriptor);
        accessor.setValue(source);
        return accessor.map(targetTypeDescriptor, getConverter());
    }

    /**
     * 将{@link Value}对象转换为目标类型的对象。
     * <p>
     * 首先尝试从类型映射表中查找匹配的转换函数；
     * 如果找到，则应用该函数进行转换；
     * 如果未找到但Value是{@link TypedValue}的实例，
     * 则使用其map方法进行类型转换；
     * 否则抛出{@link ConverterNotFoundException}。
     * </p>
     *
     * @param source 源Value对象
     * @param sourceTypeDescriptor 源Value的类型描述符
     * @param targetTypeDescriptor 目标类型描述符
     * @return 转换后的目标类型对象
     * @throws ConversionException 如果转换过程中发生错误
     * @throws ConverterNotFoundException 如果找不到合适的转换器
     */
    @Override
    public Object from(Value source, TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor)
            throws ConversionException {
        BiFunction<? super Value, ? super TypeDescriptor, ? extends Object> getter = typeMap
                .get(targetTypeDescriptor.getType());
        if (getter != null) {
            return getter.apply(source, targetTypeDescriptor);
        }

        if (source instanceof TypedValue) {
            return ((TypedValue) source).map(targetTypeDescriptor, getConverter()).get();
        }
        throw new ConverterNotFoundException(sourceTypeDescriptor, targetTypeDescriptor);
    }

}