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
	 * @param context  上下文
	 * @param executor 执行器
	 * @param args     参数
	 * @return
	 * @throws Throwable
	 */
	Object intercept(Executables context, Executable executable, Elements<? extends Object> args) throws Throwable;
}
