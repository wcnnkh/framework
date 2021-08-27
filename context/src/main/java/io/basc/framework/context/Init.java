package io.basc.framework.context;

import io.basc.framework.lang.Ignore;

@Ignore
public interface Init {
	/**
	 * 初始化时执行
	 * @throws Throwable
	 */
	void init() throws Throwable;
}
