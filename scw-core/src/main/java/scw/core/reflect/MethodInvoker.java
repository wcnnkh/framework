package scw.core.reflect;


public interface MethodInvoker extends Invoker, MethodHolder {

	Class<?> getSourceClass();

	/**
	 * 如果是静态方法那么是空
	 * 
	 * @return
	 */
	Object getInstance();
}
