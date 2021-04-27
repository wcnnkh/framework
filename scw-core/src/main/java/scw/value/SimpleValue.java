package scw.value;

import java.math.BigDecimal;
import java.math.BigInteger;

import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;

public interface SimpleValue extends Value {
	default Value getDefaultValue() {
		return EmptyValue.INSTANCE;
	}

	default int getNumberRadix() {
		return 10;
	}

	default String getAsNumberString() {
		return StringUtils.formatNumberText(getAsString());
	}

	default Byte getAsByte() {
		String v = getAsNumberString();
		if (StringUtils.hasText(v)) {
			return Byte.parseByte(v, getNumberRadix());
		}
		return getDefaultValue().getAsByte();
	}

	default byte getAsByteValue() {
		String v = getAsNumberString();
		if (StringUtils.hasText(v)) {
			return Byte.parseByte(v, getNumberRadix());
		}
		return getDefaultValue().getAsByteValue();
	}

	default Short getAsShort() {
		String v = getAsNumberString();
		if (StringUtils.hasText(v)) {
			return Short.parseShort(v, getNumberRadix());
		}
		return getDefaultValue().getAsShort();
	}

	default short getAsShortValue() {
		String v = getAsNumberString();
		if (StringUtils.hasText(v)) {
			return Short.parseShort(v, getNumberRadix());
		}
		return getDefaultValue().getAsShortValue();
	}

	default Integer getAsInteger() {
		String v = getAsNumberString();
		if (StringUtils.hasText(v)) {
			return Integer.parseInt(v, getNumberRadix());
		}
		return getDefaultValue().getAsInteger();
	}

	default int getAsIntValue() {
		String v = getAsNumberString();
		if (StringUtils.hasText(v)) {
			return Integer.parseInt(v, getNumberRadix());
		}
		return getDefaultValue().getAsIntValue();
	}

	default Long getAsLong() {
		String v = getAsNumberString();
		if (StringUtils.hasText(v)) {
			return Long.parseLong(v, getNumberRadix());
		}
		return getDefaultValue().getAsLong();
	}

	default long getAsLongValue() {
		String v = getAsNumberString();
		if (StringUtils.hasText(v)) {
			return Long.parseLong(v, getNumberRadix());
		}
		return getDefaultValue().getAsLongValue();
	}

	default Boolean getAsBoolean() {
		String v = getAsString();
		if (StringUtils.hasText(v)) {
			return StringUtils.parseBoolean(v);
		}
		return getDefaultValue().getAsBoolean();
	}

	default boolean getAsBooleanValue() {
		String v = getAsString();
		if (StringUtils.hasText(v)) {
			return StringUtils.parseBoolean(v);
		}
		return getDefaultValue().getAsBooleanValue();
	}

	default Float getAsFloat() {
		String v = getAsNumberString();
		if (StringUtils.hasText(v)) {
			return Float.parseFloat(v);
		}
		return getDefaultValue().getAsFloat();
	}

	default float getAsFloatValue() {
		String v = getAsNumberString();
		if (StringUtils.hasText(v)) {
			return Float.valueOf(v);
		}
		return getDefaultValue().getAsFloatValue();
	}

	default Double getAsDouble() {
		String v = getAsNumberString();
		if (StringUtils.hasText(v)) {
			return Double.valueOf(v);
		}
		return getDefaultValue().getAsDouble();
	}

	default double getAsDoubleValue() {
		String v = getAsNumberString();
		if (StringUtils.hasText(v)) {
			return Double.parseDouble(v);
		}
		return getDefaultValue().getAsDoubleValue();
	}

	default char getAsChar() {
		String v = getAsString();
		if (StringUtils.isEmpty(v)) {
			return getDefaultValue().getAsChar();
		}
		return v.charAt(0);
	}

	default Character getAsCharacter() {
		String v = getAsString();
		if (StringUtils.isEmpty(v)) {
			return getDefaultValue().getAsCharacter();
		}

		return v.charAt(0);
	}

	default BigInteger getAsBigInteger() {
		String v = getAsNumberString();
		if (StringUtils.hasText(v)) {
			return new BigInteger(v, getNumberRadix());
		}
		return getDefaultValue().getAsBigInteger();
	}

	default BigDecimal getAsBigDecimal() {
		String v = getAsNumberString();
		if (StringUtils.hasText(v)) {
			return new BigDecimal(v);
		}
		return getDefaultValue().getAsBigDecimal();
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
		return getDefaultValue().getAsClass();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	default Enum<?> getAsEnum(Class<?> enumType) {
		String v = getAsString();
		if (StringUtils.hasText(v)) {
			return Enum.valueOf((Class<? extends Enum>) enumType, v);
		}
		return getDefaultValue().getAsEnum(enumType);
	}

	default Number getAsNumber() {
		return getAsBigDecimal();
	}

	default boolean isNumber() {
		String value = getAsNumberString();
		if (!StringUtils.hasText(value)) {
			return false;
		}

		if (StringUtils.isNumeric(value)) {
			return true;
		}

		if (value.lastIndexOf('e') != -1 || value.lastIndexOf('E') != -1) {
			try {
				new BigDecimal(value);
				return true;
			} catch (NumberFormatException e) {
			}
		}
		return false;
	}

	default boolean isEmpty() {
		return StringUtils.isEmpty(getAsString());
	}
}
