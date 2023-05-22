package io.basc.framework.execution;

import io.basc.framework.util.Elements;

/**
 * 执行拦截器
 * 
 * @author wcnnkh
 *
 */
public interface ExecutionInterceptor {
	/**
	 * 拦截
	 * 
	 * @param source   来源
	 * @param executor 执行器
	 * @param args     参数
	 * @return
	 * @throws ExecutionException
	 */
	Object intercept(Executable source, Executor executor, Elements<? extends Object> args) throws ExecutionException;
}
