package io.basc.framework.util.reflect;

public interface MethodInvoker extends Invoker, MethodHolder {
	/**
	 * 如果是静态方法那么是空
	 * 
	 * @return 返回实例
	 */
	Object getInstance();

	/**
	 * 来源类(并不一定是方法的声明类)
	 * 
	 * @return 返回来源类
	 */
	Class<?> getSourceClass();
}
