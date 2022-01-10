package io.basc.framework.core.reflect;

public interface MethodInvoker extends Invoker, MethodHolder {
	/**
	 * 如果是静态方法那么是空
	 * 
	 * @return
	 */
	Object getInstance();

	/**
	 * 来源类(并不一定是方法的声明类)
	 * 
	 * @return
	 */
	Class<?> getSourceClass();
}
