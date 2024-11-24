package io.basc.framework.util.reflect;

@FunctionalInterface
public interface Invoker {
	Object invoke(Object... args) throws Throwable;
}
