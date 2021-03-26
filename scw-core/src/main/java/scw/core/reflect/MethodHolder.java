package scw.core.reflect;

import java.lang.reflect.Method;

public interface MethodHolder {
	/**
	 * 返回声明类，但并不一定和{@link Method#getDeclaringClass()}相同
	 * @return
	 */
	Class<?> getDeclaringClass();
	
	Method getMethod();
}