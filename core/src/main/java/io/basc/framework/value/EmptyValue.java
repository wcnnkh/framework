package io.basc.framework.value;

import io.basc.framework.convert.TypeDescriptor;

import java.io.Serializable;

public class EmptyValue extends AbstractValue implements Value, Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final EmptyValue INSTANCE = new EmptyValue();
	
	@Override
	public String getAsString() {
		return null;
	}
	
	@Override
	protected Object getAsNonBaseType(TypeDescriptor type) {
		return null;
	}
	
}
