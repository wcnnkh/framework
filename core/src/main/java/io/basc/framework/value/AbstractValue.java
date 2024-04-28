package io.basc.framework.value;

import java.util.Objects;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.TypeDescriptor;

public abstract class AbstractValue implements Value {
	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		Object unwrapSource = unwrapSource(source, sourceType);
		TypeDescriptor unwrapSourceType = sourceType;
		if (unwrapSource != source) {
			unwrapSourceType = unwrapSourceTypeDescriptor(source, sourceType, unwrapSource);
		}
		return Value.super.convert(unwrapSource, unwrapSourceType, targetType);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (obj instanceof Value) {
			Value value = (Value) obj;
			return Objects.equals(value.getSource(), getSource());
		}

		return false;
	}

	@Override
	public int hashCode() {
		Object source = getSource();
		return Objects.hashCode(source);
	}

	@Override
	public String toString() {
		Object source = getSource();
		return Objects.toString(source);
	}

	protected abstract Object unwrapSource(Object source, TypeDescriptor sourceTypeDescriptor);

	protected TypeDescriptor unwrapSourceTypeDescriptor(Object source, TypeDescriptor sourceTypeDescriptor,
			Object unwrapSource) {
		return sourceTypeDescriptor.narrow(unwrapSource);
	}
}
