package run.soeasy.framework.aop;

import lombok.NonNull;
import run.soeasy.framework.core.execute.Execution;

/**
 * 拦截器
 * 
 * @author soeasy.run
 *
 */
@FunctionalInterface
public interface ExecutionInterceptor {
	Object intercept(@NonNull Execution execution) throws Throwable;
}
