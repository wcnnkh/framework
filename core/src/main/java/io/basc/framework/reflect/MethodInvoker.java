package io.basc.framework.reflect;


public interface MethodInvoker extends Invoker, MethodHolder {
	/**
	 * 如果是静态方法那么是空
	 * 
	 * @return
	 */
	Object getInstance();
}
