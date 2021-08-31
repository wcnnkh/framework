package io.basc.framework.value;

import java.io.Serializable;

import io.basc.framework.core.ResolvableType;

public class EmptyValue extends AbstractValue implements Value, Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final EmptyValue INSTANCE = new EmptyValue();
	
	@Override
	public String getAsString() {
		return null;
	}
	
	@Override
	protected Object getAsNonBaseType(ResolvableType type) {
		return null;
	}
	
}
