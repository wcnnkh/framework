package run.soeasy.framework.core.convert.value;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Supplier;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.domain.ValueWrapper;
import run.soeasy.framework.core.domain.Version;
import run.soeasy.framework.core.math.NumberValue;

public interface TypedValueWrapper<W extends TypedValue>
		extends TypedValue, TypedDataWrapper<Object, W>, ValueWrapper<W> {

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
	default Elements<? extends TypedValue> getAsElements() {
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
	default <T> T getAsObject(Class<? extends T> requiredType, Supplier<? extends T> defaultSupplier) {
		return getSource().getAsObject(requiredType, defaultSupplier);
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

	default TypedValue value() {
		return getSource().value();
	}

	@Override
	default TypeDescriptor getReturnTypeDescriptor() {
		return getSource().getReturnTypeDescriptor();
	}

	@Override
	default Object getAsObject(TypeDescriptor type) {
		return getSource().getAsObject(type);
	}

	@Override
	default <T> TypedData<T> getAsData(Class<? extends T> requriedType) {
		return getSource().getAsData(requriedType);
	}

	@Override
	default <T> TypedData<T> getAsData(@NonNull TypeDescriptor typeDescriptor) {
		return getSource().getAsData(typeDescriptor);
	}

	@Override
	default TypedValue getAsValue(@NonNull AccessibleDescriptor typeDescriptor) {
		return getSource().getAsValue(typeDescriptor);
	}

	@Override
	default TypedValue getAsValue(@NonNull Converter converter) {
		return getSource().getAsValue(converter);
	}
}