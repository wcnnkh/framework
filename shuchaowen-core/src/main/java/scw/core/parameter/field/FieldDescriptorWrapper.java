package scw.core.parameter.field;

import java.lang.reflect.Field;

import scw.core.parameter.ParameterDescriptorWrapper;

public class FieldDescriptorWrapper extends ParameterDescriptorWrapper implements FieldDescriptor {

	public FieldDescriptorWrapper(FieldDescriptor fieldDescriptor) {
		super(fieldDescriptor);
	}

	public Field getField() {
		return ((FieldDescriptor) parameterDescriptor).getField();
	}
}
