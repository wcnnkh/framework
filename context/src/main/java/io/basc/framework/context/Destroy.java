package io.basc.framework.context;

public interface Destroy {
	/**
	 * 销毁时执行
	 * 
	 * @throws Throwable
	 */
	void destroy() throws Throwable;
}