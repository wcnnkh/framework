package io.basc.framework.value;

import java.math.BigDecimal;
import java.math.BigInteger;

import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.StringUtils;

public interface BaseValue extends Value {
	default int getNumberRadix() {
		return 10;
	}

	default String getAsNumberString() {
		return StringUtils.parseNumberText(getAsString(), getNumberRadix());
	}

	default Byte getAsByte() {
		String v = getAsNumberString();
		if (StringUtils.hasText(v)) {
			return Byte.parseByte(v, getNumberRadix());
		}
		return null;
	}

	default byte getAsByteValue() {
		String v = getAsNumberString();
		if (StringUtils.hasText(v)) {
			return Byte.parseByte(v, getNumberRadix());
		}
		return 0;
	}

	default Short getAsShort() {
		String v = getAsNumberString();
		if (StringUtils.hasText(v)) {
			return Short.parseShort(v, getNumberRadix());
		}
		return null;
	}

	default short getAsShortValue() {
		String v = getAsNumberString();
		if (StringUtils.hasText(v)) {
			return Short.parseShort(v, getNumberRadix());
		}
		return 0;
	}

	default Integer getAsInteger() {
		String v = getAsNumberString();
		if (StringUtils.hasText(v)) {
			return Integer.parseInt(v, getNumberRadix());
		}
		return null;
	}

	default int getAsIntValue() {
		String v = getAsNumberString();
		if (StringUtils.hasText(v)) {
			return Integer.parseInt(v, getNumberRadix());
		}
		return 0;
	}

	default Long getAsLong() {
		String v = getAsNumberString();
		if (StringUtils.hasText(v)) {
			return Long.parseLong(v, getNumberRadix());
		}
		return null;
	}

	default long getAsLongValue() {
		String v = getAsNumberString();
		if (StringUtils.hasText(v)) {
			return Long.parseLong(v, getNumberRadix());
		}
		return 0;
	}

	default Boolean getAsBoolean() {
		String v = getAsString();
		if (StringUtils.hasText(v)) {
			return StringUtils.parseBoolean(v);
		}
		return null;
	}

	default boolean getAsBooleanValue() {
		String v = getAsString();
		if (StringUtils.hasText(v)) {
			return StringUtils.parseBoolean(v);
		}
		return false;
	}

	default Float getAsFloat() {
		String v = getAsNumberString();
		if (StringUtils.hasText(v)) {
			return Float.parseFloat(v);
		}
		return null;
	}

	default float getAsFloatValue() {
		String v = getAsNumberString();
		if (StringUtils.hasText(v)) {
			return Float.valueOf(v);
		}
		return 0;
	}

	default Double getAsDouble() {
		String v = getAsNumberString();
		if (StringUtils.hasText(v)) {
			return Double.valueOf(v);
		}
		return null;
	}

	default double getAsDoubleValue() {
		String v = getAsNumberString();
		if (StringUtils.hasText(v)) {
			return Double.parseDouble(v);
		}
		return 0;
	}

	default char getAsChar() {
		String v = getAsString();
		if (StringUtils.isEmpty(v)) {
			return 0;
		}
		return v.charAt(0);
	}

	default Character getAsCharacter() {
		String v = getAsString();
		if (StringUtils.isEmpty(v)) {
			return null;
		}

		return v.charAt(0);
	}

	default BigInteger getAsBigInteger() {
		String v = getAsNumberString();
		if (StringUtils.hasText(v)) {
			return new BigInteger(v, getNumberRadix());
		}
		return null;
	}

	default BigDecimal getAsBigDecimal() {
		String v = getAsNumberString();
		if (StringUtils.hasText(v)) {
			return new BigDecimal(v);
		}
		return null;
	}

	default Class<?> getAsClass() {
		String v = getAsString();
		if (StringUtils.hasText(v)) {
			try {
				return ClassUtils.forName(v, null);
			} catch (ClassNotFoundException e) {
				return null;
			}
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	default Enum<?> getAsEnum(Class<?> enumType) {
		String v = getAsString();
		if (StringUtils.hasText(v)) {
			return Enum.valueOf((Class<? extends Enum>) enumType, v);
		}
		return null;
	}

	default Number getAsNumber() {
		return getAsBigDecimal();
	}

	default boolean isNumber() {
		String value = getAsNumberString();
		if (!StringUtils.hasText(value)) {
			return false;
		}

		if (StringUtils.isNumeric(value, getNumberRadix())) {
			return true;
		}

		try {
			new BigDecimal(value);
			return true;
		} catch (NumberFormatException e) {
		}
		return false;
	}

	default boolean isEmpty() {
		return StringUtils.isEmpty(getAsString());
	}
}
