package io.basc.framework.execution.param;

import java.io.Serializable;

import io.basc.framework.convert.TypeDescriptor;

public class Arg extends SimpleParameter implements Serializable {
	private static final long serialVersionUID = 1L;

	public Arg(ParameterDescriptor parameterDescriptor, Object value) {
		super(parameterDescriptor);
		setValue(value);
	}

	public Arg(int positionIndex, TypeDescriptor typeDescriptor, Object value) {
		setPositionIndex(positionIndex);
		setTypeDescriptor(typeDescriptor);
		setValue(value);
	}

	public Arg(String name, TypeDescriptor typeDescriptor, Object value) {
		setName(name);
		setTypeDescriptor(typeDescriptor);
		setValue(value);
	}
}
