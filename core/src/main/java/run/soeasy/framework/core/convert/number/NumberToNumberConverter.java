package run.soeasy.framework.core.convert.number;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.NonNull;
import run.soeasy.framework.core.NumberUtils;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.type.ClassUtils;
import run.soeasy.framework.core.type.ReflectionUtils;

/**
 * 数值类型转换器，基于 {@link NumberUtils} 实现任意 {@link Number} 子类间的安全转换，
 * 支持基础类型（byte/short/int/long/float/double）、包装类（Byte/Short等）及高精度类型（BigInteger/BigDecimal），
 * 具备「范围校验+有效数字校验+反向兜底校验」三层防护，杜绝隐式溢出与隐性精度丢失。
 *
 * @author soeasy.run
 * @see NumberUtils 高精度数值处理工具类（核心依赖，提供安全转换能力）
 * @see Converter 框架转换器接口（契约实现）
 */
public class NumberToNumberConverter implements Converter {
	public static final NumberToNumberConverter INSTANCE = new NumberToNumberConverter();

	@Override
	public boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		// 源类型和目标类型均为 Number 及其子类（含基础类型、包装类、BigInteger/BigDecimal）
		return ClassUtils.isNumber(sourceTypeDescriptor.getType())
				&& ClassUtils.isNumber(targetTypeDescriptor.getType());
	}

	@Override
	public Object convert(Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		// 1. 空值校验：Number 类型不支持 null 转换
		if (source == null) {
			throw new ConversionException("Cannot convert null to Number type");
		}

		// 2. 强转源对象为 Number（canConvert 已确保类型兼容，直接强转）
		Number sourceNumber = (Number) source;
		Class<?> targetClass = targetTypeDescriptor.getType();

		try {
			// 3. 直接根据目标类型分支转换，复用 NumberUtils 安全能力，无需额外方法拆分
			if (targetClass == byte.class || targetClass == Byte.class) {
				return NumberUtils.toByte(sourceNumber);
			} else if (targetClass == short.class || targetClass == Short.class) {
				return NumberUtils.toShort(sourceNumber);
			} else if (targetClass == int.class || targetClass == Integer.class) {
				return NumberUtils.toInteger(sourceNumber);
			} else if (targetClass == long.class || targetClass == Long.class) {
				return NumberUtils.toLong(sourceNumber);
			} else if (targetClass == float.class || targetClass == Float.class) {
				return NumberUtils.toFloat(sourceNumber);
			} else if (targetClass == double.class || targetClass == Double.class) {
				return NumberUtils.toDouble(sourceNumber);
			} else if (targetClass == BigInteger.class) {
				return NumberUtils.toBigInteger(sourceNumber);
			} else if (targetClass == BigDecimal.class) {
				return NumberUtils.toBigDecimal(sourceNumber);
			}
		} catch (ArithmeticException e) {
			// 4. 转换 NumberUtils 的算术异常（溢出、有效数字超界等）为框架统一转换异常
			throw new ConversionException(String.format("Failed to convert source Number[%s] to target type[%s]: %s",
					sourceNumber, targetClass.getName(), e.getMessage()), e);
		}

		return ReflectionUtils.newInstance(targetClass, sourceNumber);
	}
}