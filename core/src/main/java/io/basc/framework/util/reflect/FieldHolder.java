package io.basc.framework.util.reflect;

import java.lang.reflect.Field;

@FunctionalInterface
public interface FieldHolder {
	Field getField();
}
