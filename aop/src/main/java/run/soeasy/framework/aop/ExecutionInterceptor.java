package run.soeasy.framework.aop;

import lombok.NonNull;
import run.soeasy.framework.core.invoke.Execution;

/**
 * 拦截器
 * 
 * @author wcnnkh
 *
 */
@FunctionalInterface
public interface ExecutionInterceptor {
	Object intercept(@NonNull Execution execution) throws Throwable;
}
