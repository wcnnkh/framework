package run.soeasy.framework.core.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;

import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.convert.number.NumberToEnumConverter;
import run.soeasy.framework.core.convert.strings.StringToBigDecimalConverter;
import run.soeasy.framework.core.convert.strings.StringToBigIntegerConverter;
import run.soeasy.framework.core.convert.strings.StringToBooleanConverter;
import run.soeasy.framework.core.convert.strings.StringToByteConverter;
import run.soeasy.framework.core.convert.strings.StringToCharacterConverter;
import run.soeasy.framework.core.convert.strings.StringToDoubleConverter;
import run.soeasy.framework.core.convert.strings.StringToEnumConverter;
import run.soeasy.framework.core.convert.strings.StringToFloatConverter;
import run.soeasy.framework.core.convert.strings.StringToIntegerConverter;
import run.soeasy.framework.core.convert.strings.StringToLongConverter;
import run.soeasy.framework.core.convert.strings.StringToShortConverter;
import run.soeasy.framework.core.math.NumberValue;

/**
 * 统一值接口，定义了从单一数据源获取不同类型值的标准方法，支持数字、字符串、布尔值等类型的转换。
 * 该接口扩展了多个函数式接口，提供了一致的方式来获取基本数据类型的值，并通过默认方法实现了
 * 常见类型的转换逻辑，确保类型转换的一致性和安全性。
 *
 * <p>核心特性：
 * <ul>
 *   <li>类型转换：提供从原始值到各种目标类型的自动转换</li>
 *   <li>空安全：所有转换方法均支持空值处理，提供默认值</li>
 *   <li>多值支持：通过{@link #isMultiple()}和{@link #getAsElements()}支持多值场景</li>
 *   <li>数字处理：通过{@link NumberValue}接口提供统一的数字处理能力</li>
 *   <li>枚举转换：支持通过序号或名称转换为枚举类型</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>配置项解析：将配置值转换为所需的类型</li>
 *   <li>数据映射：将外部数据源的值映射为内部对象属性</li>
 *   <li>参数转换：将请求参数转换为方法参数</li>
 *   <li>表达式求值：统一处理表达式计算结果的类型转换</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 从配置获取值并转换为不同类型
 * Value configValue = configSource.getValue("timeout");
 * int timeout = configValue.getAsInt();
 * long longTimeout = configValue.getAsLong();
 * boolean enabled = configValue.getAsBoolean();
 * 
 * // 枚举转换示例
 * MyEnum enumValue = configValue.getAsEnum(MyEnum.class);
 * </pre>
 *
 * @see NumberValue
 * @see Elements
 */
public interface Value extends IntSupplier, LongSupplier, DoubleSupplier, BooleanSupplier {

    /**
     * 获取值的BigDecimal表示。
     * <p>
     * 处理逻辑：
     * <ul>
     *   <li>如果是数字类型，通过{@link NumberValue#getAsBigDecimal()}获取</li>
     *   <li>否则，通过字符串转换器将字符串值转换为BigDecimal</li>
     * </ul>
     *
     * @return 转换后的BigDecimal值
     */
    default BigDecimal getAsBigDecimal() {
        if (isNumber()) {
            return getAsNumber().getAsBigDecimal();
        }
        return StringToBigDecimalConverter.DEFAULT.convert(getAsString(), BigDecimal.class);
    }

    /**
     * 获取值的BigInteger表示。
     * <p>
     * 处理逻辑：
     * <ul>
     *   <li>如果是数字类型，通过{@link NumberValue#getAsBigInteger()}获取</li>
     *   <li>否则，通过字符串转换器将字符串值转换为BigInteger</li>
     * </ul>
     *
     * @return 转换后的BigInteger值
     */
    default BigInteger getAsBigInteger() {
        if (isNumber()) {
            return getAsNumber().getAsBigInteger();
        }
        return StringToBigIntegerConverter.DEFAULT.convert(getAsString(), BigInteger.class);
    }

    /**
     * 获取值的布尔表示。
     * <p>
     * 处理逻辑：
     * <ul>
     *   <li>如果是数字类型，通过{@link NumberValue#getAsBoolean()}获取</li>
     *   <li>否则，通过字符串转换器将字符串值转换为布尔值</li>
     *   <li>转换结果为null时返回false</li>
     * </ul>
     *
     * @return 转换后的布尔值，默认false
     */
    @Override
    default boolean getAsBoolean() {
        if (isNumber()) {
            return getAsNumber().getAsBoolean();
        }

        Boolean value = StringToBooleanConverter.DEFAULT.convert(getAsString(), Boolean.class);
        return value == null ? false : value;
    }

    /**
     * 获取值的字节表示。
     * <p>
     * 处理逻辑：
     * <ul>
     *   <li>如果是数字类型，通过{@link NumberValue#getAsByte()}获取</li>
     *   <li>否则，通过字符串转换器将字符串值转换为字节</li>
     *   <li>转换结果为null时返回0</li>
     * </ul>
     *
     * @return 转换后的字节值，默认0
     */
    default byte getAsByte() {
        if (isNumber()) {
            return getAsNumber().getAsByte();
        }

        Byte value = StringToByteConverter.DEFAULT.convert(getAsString(), Byte.class);
        return value == null ? 0 : value;
    }

    /**
     * 获取值的字符表示。
     * <p>
     * 处理逻辑：
     * <ul>
     *   <li>通过字符串转换器将字符串值转换为字符</li>
     *   <li>转换结果为null时返回0</li>
     * </ul>
     *
     * @return 转换后的字符值，默认0
     */
    default char getAsChar() {
        Character value = StringToCharacterConverter.DEFAULT.convert(getAsString(), Character.class);
        return value == null ? 0 : value;
    }

    /**
     * 获取值的双精度浮点表示。
     * <p>
     * 处理逻辑：
     * <ul>
     *   <li>如果是数字类型，通过{@link NumberValue#getAsDouble()}获取</li>
     *   <li>否则，通过字符串转换器将字符串值转换为双精度浮点数</li>
     *   <li>转换结果为null时返回0d</li>
     * </ul>
     *
     * @return 转换后的双精度浮点值，默认0d
     */
    @Override
    default double getAsDouble() {
        if (isNumber()) {
            return getAsNumber().getAsDouble();
        }

        Double value = StringToDoubleConverter.DEFAULT.convert(getAsString(), Double.class);
        return value == null ? 0d : value;
    }

    /**
     * 判断值是否为多值类型。
     * <p>
     * 默认实现返回false，表示单值类型。
     * 多值类型可通过{@link #getAsElements()}获取元素集合。
     *
     * @return true如果是多值类型，false否则
     */
    default boolean isMultiple() {
        return false;
    }

    /**
     * 获取值的元素集合表示。
     * <p>
     * 默认实现返回空集合。多值类型应重写此方法返回实际元素集合。
     *
     * @return 元素集合，默认空集合
     */
    default Elements<? extends Value> getAsElements() {
        return Elements.empty();
    }

    /**
     * 将值转换为枚举类型。
     * <p>
     * 处理逻辑：
     * <ul>
     *   <li>如果是数字类型，通过序号转换为枚举</li>
     *   <li>否则，通过名称转换为枚举</li>
     * </ul>
     *
     * @param <T>      枚举类型
     * @param enumType 枚举类
     * @return 转换后的枚举值，可能为null
     */
    default <T extends Enum<T>> T getAsEnum(Class<T> enumType) {
        if (isNumber()) {
            return NumberToEnumConverter.DEFAULT.convert(getAsNumber(), enumType);
        }
        return StringToEnumConverter.DEFAULT.convert(getAsString(), enumType);
    }

    /**
     * 获取值的单精度浮点表示。
     * <p>
     * 处理逻辑：
     * <ul>
     *   <li>如果是数字类型，通过{@link NumberValue#getAsFloat()}获取</li>
     *   <li>否则，通过字符串转换器将字符串值转换为单精度浮点数</li>
     *   <li>转换结果为null时返回0f</li>
     * </ul>
     *
     * @return 转换后的单精度浮点值，默认0f
     */
    default float getAsFloat() {
        if (isNumber()) {
            return getAsNumber().getAsFloat();
        }

        Float value = StringToFloatConverter.DEFAULT.convert(getAsString(), Float.class);
        return value == null ? 0f : value;
    }

    /**
     * 获取值的整数表示。
     * <p>
     * 处理逻辑：
     * <ul>
     *   <li>如果是数字类型，通过{@link NumberValue#getAsInt()}获取</li>
     *   <li>否则，通过字符串转换器将字符串值转换为整数</li>
     *   <li>转换结果为null时返回0</li>
     * </ul>
     *
     * @return 转换后的整数值，默认0
     */
    @Override
    default int getAsInt() {
        if (isNumber()) {
            return getAsNumber().getAsInt();
        }

        Integer value = StringToIntegerConverter.DEFAULT.convert(getAsString(), Integer.class);
        return value == null ? 0 : value;
    }

    /**
     * 获取值的长整数表示。
     * <p>
     * 处理逻辑：
     * <ul>
     *   <li>如果是数字类型，通过{@link NumberValue#getAsLong()}获取</li>
     *   <li>否则，通过字符串转换器将字符串值转换为长整数</li>
     *   <li>转换结果为null时返回0L</li>
     * </ul>
     *
     * @return 转换后的长整数值，默认0L
     */
    @Override
    default long getAsLong() {
        if (isNumber()) {
            return getAsNumber().getAsLong();
        }

        Long value = StringToLongConverter.DEFAULT.convert(getAsString(), Long.class);
        return value == null ? 0L : value;
    }

    /**
     * 获取值的短整数表示。
     * <p>
     * 处理逻辑：
     * <ul>
     *   <li>如果是数字类型，通过{@link NumberValue#getAsShort()}获取</li>
     *   <li>否则，通过字符串转换器将字符串值转换为短整数</li>
     *   <li>转换结果为null时返回0</li>
     * </ul>
     *
     * @return 转换后的短整数值，默认0
     */
    default short getAsShort() {
        if (isNumber()) {
            return getAsNumber().getAsShort();
        }

        Short value = StringToShortConverter.DEFAULT.convert(getAsString(), Short.class);
        return value == null ? 0 : value;
    }

    /**
     * 获取值的版本表示。
     * <p>
     * 处理逻辑：
     * <ul>
     *   <li>如果是数字类型，直接转换为版本</li>
     *   <li>否则，通过字符串模板转换为版本</li>
     * </ul>
     *
     * @return 转换后的版本对象
     */
    default Version getAsVersion() {
        return isNumber() ? getAsNumber() : new CharSequenceTemplate(getAsString(), null);
    }

    /**
     * 判断值是否为数字类型。
     *
     * @return true如果是数字类型，false否则
     */
    boolean isNumber();

    /**
     * 获取值的数字表示。
     * <p>
     * 调用前应先通过{@link #isNumber()}判断是否为数字类型。
     *
     * @return 数字值接口，可能为null
     */
    NumberValue getAsNumber();

    /**
     * 获取值的字符串表示。
     *
     * @return 字符串值，不会为null
     */
    String getAsString();
}