package io.basc.framework.context;

import io.basc.framework.lang.Ignore;

@Ignore
public interface Destroy {
	/**
	 * 销毁时执行
	 * @throws Throwable
	 */
	void destroy() throws Throwable;
}