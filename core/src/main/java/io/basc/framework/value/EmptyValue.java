package io.basc.framework.value;

import java.io.Serializable;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ConversionFailedException;
import io.basc.framework.convert.TypeDescriptor;

public class EmptyValue implements Value, Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public Object getSource() {
		return null;
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		throw new ConversionFailedException(sourceType, targetType, source, null);
	}
}
