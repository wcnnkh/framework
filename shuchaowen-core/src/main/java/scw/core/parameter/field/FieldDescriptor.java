package scw.core.parameter.field;

import java.lang.reflect.Field;

import scw.core.parameter.ParameterDescriptor;

public interface FieldDescriptor extends ParameterDescriptor {
	public static final FieldDescriptor[] EMPTY_ARRAY = new FieldDescriptor[0];

	Field getField();
}
