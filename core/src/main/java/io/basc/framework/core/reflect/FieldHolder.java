package io.basc.framework.core.reflect;

import java.lang.reflect.Field;

public interface FieldHolder {
	/**
	 * 返回声明类，但并不一定和{@link Field#getDeclaringClass()}相同
	 * @return
	 */
	Class<?> getDeclaringClass();
	
	Field getField();
}
