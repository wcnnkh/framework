package io.basc.framework.aop;

@FunctionalInterface
public interface AopPolicy {
	boolean isProxy(Object instance);
}