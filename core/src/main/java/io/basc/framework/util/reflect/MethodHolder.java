package io.basc.framework.util.reflect;

import java.lang.reflect.Method;

@FunctionalInterface
public interface MethodHolder {
	Method getMethod();
}