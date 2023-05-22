package io.basc.framework.execution.aop;

import io.basc.framework.execution.Executable;
import io.basc.framework.execution.ExecutionInterceptor;
import io.basc.framework.execution.Executor;
import io.basc.framework.execution.Executors;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ServiceRegistry;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.XUtils;

public class Aop extends Proxies {
	private final String id;
	private final ServiceRegistry<ExecutionInterceptor> executionInterceptorRegistry = new ServiceRegistry<>();

	public ServiceRegistry<ExecutionInterceptor> getExecutionInterceptorRegistry() {
		return executionInterceptorRegistry;
	}

	public Aop() {
		this.id = XUtils.getUUID();
	}

	public final String getId() {
		return id;
	}

	public boolean isProxy(Object instance) {
		if (instance instanceof DelegatedObject) {
			return StringUtils.equals(((DelegatedObject) instance).getProxyContainerId(), this.id);
		}
		return false;
	}

	@Override
	public Executors getProxy(Class<?> sourceClass, Class<?>[] interfaces,
			@Nullable ExecutionInterceptor executionInterceptor) {
		// 合并默认的拦截器
		return super.getProxy(sourceClass, interfaces, executionInterceptor);
	}

	public final Executable getProxy(Class<?> sourceClass) {
		return getProxy(sourceClass, null, null);
	}

	public final Executor getProxyExecutor(Executor executor) {
		return getProxyExecutor(executor, null);
	}

	public Executor getProxyExecutor(Executor executor, @Nullable ExecutionInterceptor executionInterceptor) {
		// 代理一个拦截器
		return null;
	}

	@Override
	public String toString() {
		return id;
	}
}
