package io.basc.framework.core.reflect;

import java.lang.reflect.Method;

@FunctionalInterface
public interface MethodHolder {
	Method getMethod();
}