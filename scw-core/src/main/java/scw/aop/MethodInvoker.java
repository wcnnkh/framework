package scw.aop;

import scw.core.reflect.MethodHolder;

public interface MethodInvoker extends Invoker, MethodHolder {

	Class<?> getSourceClass();

	/**
	 * 如果是静态方法那么是空
	 * 
	 * @return
	 */
	Object getInstance();
}
