package io.basc.framework.core.reflect;

import java.lang.reflect.Field;

@FunctionalInterface
public interface FieldHolder {
	Field getField();
}
