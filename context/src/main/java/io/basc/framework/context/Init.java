package io.basc.framework.context;

public interface Init {
	/**
	 * 初始化时执行
	 * @throws Throwable
	 */
	void init() throws Throwable;
}
