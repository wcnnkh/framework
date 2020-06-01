package scw.aop;

import java.lang.reflect.Method;

public interface MethodInvoker extends Invoker {
	Method getMethod();

	Class<?> getTargetClass();
}
