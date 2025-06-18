package run.soeasy.framework.core.domain;

import java.math.BigDecimal;
import java.math.BigInteger;

import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.math.NumberValue;

@FunctionalInterface
public interface ValueWrapper<W extends Value> extends Value, Wrapper<W> {
	@Override
	default <T> T getAsArray(Class<? extends T> componentType) {
		return getSource().getAsArray(componentType);
	}

	@Override
	default BigDecimal getAsBigDecimal() {
		return getSource().getAsBigDecimal();
	}

	@Override
	default BigInteger getAsBigInteger() {
		return getSource().getAsBigInteger();
	}

	@Override
	default boolean getAsBoolean() {
		return getSource().getAsBoolean();
	}

	@Override
	default byte getAsByte() {
		return getSource().getAsByte();
	}

	@Override
	default char getAsChar() {
		return getSource().getAsChar();
	}

	@Override
	default double getAsDouble() {
		return getSource().getAsDouble();
	}

	@Override
	default Elements<? extends Value> getAsElements() {
		return getSource().getAsElements();
	}

	@Override
	default <T extends Enum<T>> T getAsEnum(Class<T> enumType) {
		return getSource().getAsEnum(enumType);
	}

	@Override
	default float getAsFloat() {
		return getSource().getAsFloat();
	}

	@Override
	default int getAsInt() {
		return getSource().getAsInt();
	}

	@Override
	default long getAsLong() {
		return getSource().getAsLong();
	}

	@Override
	default NumberValue getAsNumber() {
		return getSource().getAsNumber();
	}

	@Override
	default <R> R getAsObject(Class<? extends R> requiredType) {
		return getSource().getAsObject(requiredType);
	}

	@Override
	default short getAsShort() {
		return getSource().getAsShort();
	}

	@Override
	default String getAsString() {
		return getSource().getAsString();
	}

	@Override
	default Version getAsVersion() {
		return getSource().getAsVersion();
	}

	@Override
	default boolean isMultiple() {
		return getSource().isMultiple();
	}

	@Override
	default boolean isNumber() {
		return getSource().isNumber();
	}
}
