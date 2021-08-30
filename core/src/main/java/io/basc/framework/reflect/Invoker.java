package io.basc.framework.reflect;

@FunctionalInterface
public interface Invoker {
	Object invoke(Object... args) throws Throwable;
}
