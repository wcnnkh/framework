package scw.orm;

import java.lang.reflect.Method;

import scw.core.reflect.FieldDefinition;

public interface Column extends FieldDefinition {
	Method getGetterMethod();

	Method getSetterMethod();
}
