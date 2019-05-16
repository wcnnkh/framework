package scw.core.reflect;

import java.lang.reflect.Method;

public interface MethodDefinition {
	Object invoke(Object obj, Object... args) throws Throwable;
	
	Method getMethod();
}
