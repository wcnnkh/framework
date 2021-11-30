package io.basc.framework.value;

import java.math.BigDecimal;
import java.math.BigInteger;

import io.basc.framework.util.ClassUtils;

public class ValueUtils {
	/**
	 * 这并不是指基本数据类型，这是指Value可以直接转换的类型
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isBaseType(Class<?> type) {
		if (type == null) {
			return false;
		}
		return ClassUtils.isPrimitiveOrWrapper(type) || type == String.class || type == BigDecimal.class
				|| type == BigInteger.class || Number.class == type || type.isEnum() || type == Class.class;
	}
}
