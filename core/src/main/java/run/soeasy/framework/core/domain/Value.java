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

public interface Value extends IntSupplier, LongSupplier, DoubleSupplier, BooleanSupplier {

	default BigDecimal getAsBigDecimal() {
		if (isNumber()) {
			return getAsNumber().getAsBigDecimal();
		}
		return StringToBigDecimalConverter.DEFAULT.convert(getAsString(), BigDecimal.class);
	}

	default BigInteger getAsBigInteger() {
		if (isNumber()) {
			return getAsNumber().getAsBigInteger();
		}
		return StringToBigIntegerConverter.DEFAULT.convert(getAsString(), BigInteger.class);
	}

	@Override
	default boolean getAsBoolean() {
		if (isNumber()) {
			return getAsNumber().getAsBoolean();
		}

		Boolean value = StringToBooleanConverter.DEFAULT.convert(getAsString(), Boolean.class);
		return value == null ? false : value;
	}

	default byte getAsByte() {
		if (isNumber()) {
			return getAsNumber().getAsByte();
		}

		Byte value = StringToByteConverter.DEFAULT.convert(getAsString(), Byte.class);
		return value == null ? 0 : value;
	}

	default char getAsChar() {
		Character value = StringToCharacterConverter.DEFAULT.convert(getAsString(), Character.class);
		return value == null ? 0 : value;
	}

	@Override
	default double getAsDouble() {
		if (isNumber()) {
			return getAsNumber().getAsDouble();
		}

		Double value = StringToDoubleConverter.DEFAULT.convert(getAsString(), Double.class);
		return value == null ? 0d : value;
	}

	default boolean isMultiple() {
		return false;
	}

	default Elements<? extends Value> getAsElements() {
		return Elements.empty();
	}

	default <T extends Enum<T>> T getAsEnum(Class<T> enumType) {
		if (isNumber()) {
			return NumberToEnumConverter.DEFAULT.convert(getAsNumber(), enumType);
		}
		return StringToEnumConverter.DEFAULT.convert(getAsString(), enumType);
	}

	default float getAsFloat() {
		if (isNumber()) {
			return getAsNumber().getAsFloat();
		}

		Float value = StringToFloatConverter.DEFAULT.convert(getAsString(), Float.class);
		return value == null ? 0f : value;
	}

	@Override
	default int getAsInt() {
		if (isNumber()) {
			return getAsNumber().getAsInt();
		}

		Integer value = StringToIntegerConverter.DEFAULT.convert(getAsString(), Integer.class);
		return value == null ? 0 : value;
	}

	@Override
	default long getAsLong() {
		if (isNumber()) {
			return getAsNumber().getAsLong();
		}

		Long value = StringToLongConverter.DEFAULT.convert(getAsString(), Long.class);
		return value == null ? 0L : value;
	}

	default short getAsShort() {
		if (isNumber()) {
			return getAsNumber().getAsShort();
		}

		Short value = StringToShortConverter.DEFAULT.convert(getAsString(), Short.class);
		return value == null ? 0 : value;
	}

	default Version getAsVersion() {
		return isNumber() ? getAsNumber() : new CharSequenceTemplate(getAsString(), null);
	}

	boolean isNumber();

	NumberValue getAsNumber();

	String getAsString();
}
