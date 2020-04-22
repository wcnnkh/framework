package scw.core.parameter.field;

import java.lang.reflect.Field;

import scw.core.parameter.ParameterDescriptor;

public interface FieldDescriptor extends ParameterDescriptor {
	Field getField();
}
