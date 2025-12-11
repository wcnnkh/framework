package run.soeasy.framework.core.convert.value;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;

import lombok.NonNull;
import run.soeasy.framework.core.collection.ArrayUtils;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.domain.BigDecimalValue;
import run.soeasy.framework.core.domain.CharSequenceTemplate;
import run.soeasy.framework.core.domain.NumberValue;
import run.soeasy.framework.core.domain.Value;
import run.soeasy.framework.core.domain.Version;
import run.soeasy.framework.core.streaming.Streamable;
import run.soeasy.framework.core.type.ClassUtils;

/**
 * 类型化值接口，整合类型化数据与值对象功能，提供统一的类型安全值访问和转换能力。
 * <p>
 * 该接口继承自{@link TypedData}和{@link Value}，既提供类型元信息描述能力，
 * 又支持多种数据类型的强转操作，适用于需要类型安全值处理的场景，如数据绑定、类型转换、值对象操作等。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>类型安全访问：通过{@link TypeDescriptor}获取完整的类型元信息</li>
 *   <li>多类型转换：支持基本类型、大数、枚举、集合等多种类型的转换</li>
 *   <li>集合处理：自动处理数组、集合、Iterable等聚合类型</li>
 *   <li>链式操作：通过{@link #map}方法支持类型转换的链式调用</li>
 *   <li>空值安全：所有转换方法均处理空值情况，避免NPE</li>
 * </ul>
 *
 * @author soeasy.run
 * @see TypedData
 * @see Value
 * @see TypeDescriptor
 */
public interface TypedValue extends TypedData<Object>, Value {

    /**
     * 创建类型化值实例（自动推导类型）
     * <p>
     * 自动推导值的类型，适用于简单类型场景。若值本身是{@link TypedData}，
     * 则直接返回其{@link TypedValue}表示；否则创建新的类型化值实例。
     * 
     * @param value 数据值
     * @return 类型化值实例
     */
    public static TypedValue of(Object value) {
        return of(value, null);
    }

    /**
     * 创建类型化值实例（支持显式类型指定）
     * <p>
     * 允许显式指定类型描述符，适用于泛型类型、数组类型或需要明确类型上下文的场景。
     * 若值本身是{@link TypedData}，则使用其类型描述符或覆盖为指定类型；
     * 否则根据值和指定类型创建新的类型化值实例。
     * 
     * @param value          数据值
     * @param typeDescriptor 类型描述符（可为null，自动推导类型）
     * @return 类型化值实例
     */
    public static TypedValue of(Object value, TypeDescriptor typeDescriptor) {
        if (value instanceof TypedData) {
            TypedValue typedValue = ((TypedData<?>) value).value();
            return typeDescriptor == null ? typedValue : typedValue.map(typeDescriptor, Converter.assignable());
        }

        CustomizeTypedValueAccessor any = new CustomizeTypedValueAccessor();
        any.setValue(value);
        any.setTypeDescriptor(typeDescriptor);
        return any;
    }

    /**
     * 获取BigDecimal类型的值
     * <p>
     * 转换逻辑：
     * <ol>
     *   <li>若值本身是BigDecimal，直接返回</li>
     *   <li>若值是BigInteger，转换为BigDecimal</li>
     *   <li>若值是Value，调用其getAsBigDecimal方法</li>
     *   <li>若值是Number，转换为BigDecimal</li>
     *   <li>否则使用转换器转换为BigDecimal</li>
     * </ol>
     * 
     * @return BigDecimal类型的值，值为null时返回null
     */
    default BigDecimal getAsBigDecimal() {
        Object value = get();
        if (value == null) {
            return null;
        }

        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }

        if (value instanceof BigInteger) {
            return new BigDecimal((BigInteger) value);
        }

        if (value instanceof Value) {
            return ((Value) value).getAsBigDecimal();
        }

        if (value instanceof Number) {
            return new BigDecimal(((Number) value).doubleValue());
        }
        return map(BigDecimal.class, Converter.assignable()).get();
    }

    /**
     * 获取BigInteger类型的值
     * <p>
     * 转换逻辑：
     * <ol>
     *   <li>若值本身是BigInteger，直接返回</li>
     *   <li>若值是BigDecimal，转换为BigInteger</li>
     *   <li>若值是Value，调用其getAsBigInteger方法</li>
     *   <li>否则使用转换器转换为BigInteger</li>
     * </ol>
     * 
     * @return BigInteger类型的值，值为null时返回null
     */
    default BigInteger getAsBigInteger() {
        Object value = get();
        if (value == null) {
            return null;
        }

        if (value instanceof BigInteger) {
            return (BigInteger) value;
        }

        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).toBigInteger();
        }

        if (value instanceof Value) {
            return ((Value) value).getAsBigInteger();
        }
        return map(BigInteger.class, Converter.assignable()).get();
    }

    /**
     * 获取boolean类型的值
     * <p>
     * 转换逻辑：
     * <ol>
     *   <li>若值本身是Boolean，直接返回</li>
     *   <li>若值是Value，调用其getAsBoolean方法</li>
     *   <li>若值是Number，非0为true</li>
     *   <li>否则使用转换器转换为boolean</li>
     * </ol>
     * 
     * @return boolean类型的值，值为null时返回false
     */
    default boolean getAsBoolean() {
        Object value = get();
        if (value == null) {
            return false;
        }

        if (value instanceof Boolean) {
            return (Boolean) value;
        }

        if (value instanceof Value) {
            return ((Value) value).getAsBoolean();
        }

        if (value instanceof Number) {
            return ((Number) value).intValue() == 1;
        }
        Boolean target = map(boolean.class, Converter.assignable()).get();
        return target == null ? false : target;
    }

    /**
     * 获取byte类型的值
     * <p>
     * 转换逻辑：
     * <ol>
     *   <li>若值本身是Byte，直接返回</li>
     *   <li>若值是Value，调用其getAsByte方法</li>
     *   <li>若值是Number，转换为byte</li>
     *   <li>否则使用转换器转换为byte</li>
     * </ol>
     * 
     * @return byte类型的值，值为null时返回0
     */
    default byte getAsByte() {
        Object value = get();
        if (value == null) {
            return 0;
        }

        if (value instanceof Byte) {
            return (Byte) value;
        }

        if (value instanceof Value) {
            return ((Value) value).getAsByte();
        }

        if (value instanceof Number) {
            return ((Number) value).byteValue();
        }
        Byte target = map(byte.class, Converter.assignable()).get();
        return target == null ? 0 : target;
    }

    /**
     * 获取char类型的值
     * <p>
     * 转换逻辑：
     * <ol>
     *   <li>若值本身是Character，直接返回</li>
     *   <li>若值是Value，调用其getAsChar方法</li>
     *   <li>否则使用转换器转换为char</li>
     * </ol>
     * 
     * @return char类型的值，值为null时返回'\0'
     */
    default char getAsChar() {
        Object value = get();
        if (value == null) {
            return 0;
        }

        if (value instanceof Character) {
            return (Character) value;
        }

        if (value instanceof Value) {
            return ((Value) value).getAsChar();
        }
        Character target = map(char.class, Converter.assignable()).get();
        return target == null ? 0 : target;
    }

    /**
     * 获取double类型的值（重写Value接口方法）
     * <p>
     * 转换逻辑与{@link #getAsDouble()}一致，提供统一的Value接口实现。
     */
    @Override
    default double getAsDouble() {
        Object value = get();
        if (value == null) {
            return 0;
        }

        if (value instanceof Double) {
            return (Double) value;
        }

        if (value instanceof Value) {
            return ((Value) value).getAsDouble();
        }

        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        Double target = map(double.class, Converter.assignable()).get();
        return target == null ? 0d : target;
    }

    /**
     * 获取元素集合（重写Value接口方法）
     * <p>
     * 自动处理多种聚合类型：
     * <ol>
     *   <li>Collection：直接转换为Elements</li>
     *   <li>Iterable：转换为Elements并保留类型信息</li>
     *   <li>Enumerable：转换为Elements并保留类型信息</li>
     *   <li>数组：转换为Elements并保留元素类型</li>
     *   <li>其他：作为单元素集合处理</li>
     * </ol>
     */
    @Override
    default Streamable<? extends TypedValue> getAsElements() {
        Object value = get();
        TypeDescriptor typeDescriptor = getReturnTypeDescriptor();
        if (value instanceof Collection) {
            Collection<?> collection = (Collection<?>) value;
            TypeDescriptor elementTypeDescriptor = typeDescriptor.getElementTypeDescriptor();
            return Streamable.of(collection).map((v) -> TypedValue.of(v, elementTypeDescriptor));
        } else if (value instanceof Iterable) {
            Iterable<?> iterable = (Iterable<?>) value;
            TypeDescriptor elementTypeDescriptor = typeDescriptor.upcast(Iterable.class)
                    .map((e) -> e.getActualTypeArgument(0));
            return Streamable.of(iterable).map((v) -> TypedValue.of(v, elementTypeDescriptor));
        } else if (value.getClass().isArray()) {
            TypeDescriptor elementTypeDescriptor = typeDescriptor.getElementTypeDescriptor();
            return Streamable.of(() -> ArrayUtils.stream(value).map((e) -> TypedValue.of(e, elementTypeDescriptor)));
        }
        return Streamable.singleton(TypedValue.of(value, typeDescriptor));
    }

    /**
     * 获取枚举类型的值
     * <p>
     * 转换逻辑：
     * <ol>
     *   <li>若值本身是Enum，直接转换为指定类型</li>
     *   <li>若值是Value，调用其getAsEnum方法</li>
     *   <li>否则使用转换器转换为指定枚举类型</li>
     * </ol>
     * 
     * @param enumType 枚举类型
     * @return 枚举值，值为null时返回null
     */
    @SuppressWarnings("unchecked")
    @Override
    default <T extends Enum<T>> T getAsEnum(Class<T> enumType) {
        Object value = get();
        if (value == null) {
            return null;
        }

        if (value instanceof Enum<?>) {
            return (T) value;
        }
        if (value instanceof Value) {
            return ((Value) value).getAsEnum(enumType);
        }
        return map(enumType, Converter.assignable()).get();
    }

    /**
     * 获取float类型的值
     * <p>
     * 转换逻辑：
     * <ol>
     *   <li>若值本身是Float，直接返回</li>
     *   <li>若值是Number，转换为float</li>
     *   <li>若值是TypedValue，调用其getAsFloat方法</li>
     *   <li>否则使用转换器转换为float</li>
     * </ol>
     * 
     * @return float类型的值，值为null时返回0
     */
    default float getAsFloat() {
        Object value = get();
        if (value == null) {
            return 0;
        }

        if (value instanceof Float) {
            return (Float) value;
        }

        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }

        if (value instanceof TypedValue) {
            return ((TypedValue) value).getAsFloat();
        }
        Float target = map(float.class, Converter.assignable()).get();
        return target == null ? 0f : target;
    }

    /**
     * 获取int类型的值（重写Value接口方法）
     * <p>
     * 转换逻辑：
     * <ol>
     *   <li>若值本身是Integer，直接返回</li>
     *   <li>若值是Value，调用其getAsInt方法</li>
     *   <li>若值是Number，转换为int</li>
     *   <li>否则使用转换器转换为int</li>
     * </ol>
     */
    @Override
    default int getAsInt() {
        Object value = get();
        if (value == null) {
            return 0;
        }

        if (value instanceof Integer) {
            return (Integer) value;
        }

        if (value instanceof Value) {
            return ((Value) value).getAsInt();
        }

        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        Integer target = map(int.class, Converter.assignable()).get();
        return target == null ? 0 : target;
    }

    /**
     * 获取long类型的值（重写Value接口方法）
     * <p>
     * 转换逻辑：
     * <ol>
     *   <li>若值本身是Long，直接返回</li>
     *   <li>若值是Value，调用其getAsLong方法</li>
     *   <li>若值是Number，转换为long</li>
     *   <li>否则使用转换器转换为long</li>
     * </ol>
     */
    @Override
    default long getAsLong() {
        Object value = get();
        if (value == null) {
            return 0;
        }

        if (value instanceof Long) {
            return (Long) value;
        }

        if (value instanceof Value) {
            return ((Value) value).getAsLong();
        }

        if (value instanceof Number) {
            return ((Number) value).longValue();
        }

        Long target = map(long.class, Converter.assignable()).get();
        return target == null ? 0L : target;
    }

    /**
     * 获取NumberValue类型的值
     * <p>
     * 转换逻辑：
     * <ol>
     *   <li>若值是String，转换为BigDecimalValue</li>
     *   <li>若值是Number，转换为BigDecimalValue</li>
     *   <li>若值是Value，调用其getAsNumber方法</li>
     *   <li>否则使用转换器转换为NumberValue</li>
     * </ol>
     * 
     * @return NumberValue类型的值，值为null时返回null
     */
    default NumberValue getAsNumber() {
        Object value = get();
        if (value == null) {
            return null;
        }

        if (value instanceof String) {
            return new BigDecimalValue(getAsString());
        }

        if (value instanceof Number) {
            return new BigDecimalValue(getAsString());
        }

        if (value instanceof Value) {
            return ((Value) value).getAsNumber();
        }

        return map(NumberValue.class, Converter.assignable()).get();
    }

    /**
     * 获取short类型的值
     * <p>
     * 转换逻辑：
     * <ol>
     *   <li>若值本身是Short，直接返回</li>
     *   <li>若值是Value，调用其getAsShort方法</li>
     *   <li>若值是Number，转换为short</li>
     *   <li>否则使用转换器转换为short</li>
     * </ol>
     * 
     * @return short类型的值，值为null时返回0
     */
    default short getAsShort() {
        Object value = get();
        if (value == null) {
            return 0;
        }

        if (value instanceof Short) {
            return (Short) value;
        }

        if (value instanceof Value) {
            return ((Value) value).getAsShort();
        }

        if (value instanceof Number) {
            return ((Number) value).shortValue();
        }
        Short target = map(short.class, Converter.assignable()).get();
        return target == null ? 0 : target;
    }

    /**
     * 获取String类型的值
     * <p>
     * 转换逻辑：
     * <ol>
     *   <li>若值本身是String，直接返回</li>
     *   <li>若值是Value，调用其getAsString方法</li>
     *   <li>若值是Enum，返回其name</li>
     *   <li>否则使用转换器转换为String</li>
     * </ol>
     * 
     * @return String类型的值，值为null时返回null
     */
    default String getAsString() {
        Object value = get();
        if (value == null) {
            return null;
        }

        if (value instanceof String) {
            return (String) value;
        }

        if (value instanceof Value) {
            return ((Value) value).getAsString();
        }

        if (value instanceof Enum) {
            return ((Enum<?>) value).name();
        }

        return map(String.class, Converter.assignable()).get();
    }

    /**
     * 获取Version类型的值（重写Value接口方法）
     * <p>
     * 转换逻辑：
     * <ol>
     *   <li>若值是CharSequence，转换为CharSequenceTemplate</li>
     *   <li>若值是Value，调用其getAsVersion方法</li>
     *   <li>否则使用转换器转换为Version</li>
     * </ol>
     */
    @Override
    default Version getAsVersion() {
        Object value = get();
        if (value == null) {
            return null;
        }

        if (value instanceof CharSequence) {
            return new CharSequenceTemplate((CharSequence) value);
        }

        if (value instanceof Value) {
            return ((Value) value).getAsVersion();
        }

        return map(Version.class, Converter.assignable()).get();
    }

    /**
     * 判断是否为多值类型（重写Value接口方法）
     * <p>
     * 判断依据：
     * <ol>
     *   <li>类型描述符是集合类型</li>
     *   <li>类型描述符是数组类型</li>
     *   <li>类型是Iterable或其子类型</li>
     *   <li>类型是Enumerable或其子类型</li>
     * </ol>
     */
    @Override
    default boolean isMultiple() {
        TypeDescriptor typeDescriptor = getReturnTypeDescriptor();
        return typeDescriptor.isCollection() || typeDescriptor.isArray()
                || Iterable.class.isAssignableFrom(typeDescriptor.getType());
    }

    /**
     * 判断是否为数字类型
     * <p>
     * 判断依据：
     * <ol>
     *   <li>类型描述符是数字类型</li>
     *   <li>值本身是Number类型</li>
     *   <li>值是Value且其isNumber返回true</li>
     * </ol>
     * 
     * @return true表示是数字类型，否则false
     * @see #getAsNumber()
     */
    default boolean isNumber() {
        TypeDescriptor typeDescriptor = getReturnTypeDescriptor();
        if (ClassUtils.isNumber(typeDescriptor.getType())) {
            return true;
        }

        Object value = get();
        if (value instanceof Number) {
            return true;
        }

        if (value instanceof Value) {
            return ((Value) value).isNumber();
        }
        return false;
    }

    /**
     * 转换为指定类型的TypedData（泛型方法）
     * <p>
     * 调用{@link #map(TypeDescriptor, Converter)}方法，
     * 使用{@link TypeDescriptor#forObject(Object)}创建类型描述符。
     * 
     * @param <R>        目标类型
     * @param type       目标类型Class
     * @param converter  转换器
     * @return 转换后的TypedData实例
     */
    @SuppressWarnings("unchecked")
    default <R> TypedData<R> map(@NonNull Class<R> type, @NonNull Converter converter) {
        return (TypedData<R>) map(TypeDescriptor.forObject(type), converter);
    }

    /**
     * 转换为指定类型的TypedValue
     * <p>
     * 使用指定的转换器将当前值转换为目标类型，返回新的TypedValue实例。
     * 转换过程中保留原始值的类型信息，并应用转换器进行类型转换。
     * 
     * @param typeDescriptor 目标类型描述符
     * @param converter      转换器
     * @return 转换后的TypedValue实例
     */
    default TypedValue map(@NonNull TypeDescriptor typeDescriptor, @NonNull Converter converter) {
        return new MappedTypedValue<>(this, typeDescriptor, converter);
    }

    /**
     * 返回当前实例（实现Value接口）
     * <p>
     * 实现{@link TypedData#value()}方法，直接返回当前实例，
     * 支持链式调用和统一的Value接口操作。
     * 
     * @return 当前TypedValue实例
     */
    @Override
    default TypedValue value() {
        return this;
    }
}