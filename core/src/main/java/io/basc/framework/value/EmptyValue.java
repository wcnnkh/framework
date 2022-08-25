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
	public Object getAsObject(TypeDescriptor type) {
		if(Value.isBaseType(type.getType())) {
			return getAsObject(type.getType());
		}
		return null;
	}

	@Override
	public Object get() {
		return null;
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		return TypeDescriptor.valueOf(Object.class);
	}
}
