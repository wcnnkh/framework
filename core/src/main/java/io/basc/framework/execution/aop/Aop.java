package io.basc.framework.execution.aop;

import io.basc.framework.execution.Executor;
import io.basc.framework.execution.ExecutionInterceptor;
import io.basc.framework.execution.Executable;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ServiceRegistry;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.XUtils;

public class Aop extends ProxyFactoryRegistry {
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
	public Proxy getProxy(Class<?> sourceClass, Class<?>[] interfaces,
			@Nullable ExecutionInterceptor executionInterceptor) {
		// 合并默认的拦截器
		return super.getProxy(sourceClass, interfaces, executionInterceptor);
	}

	public final Executor getProxy(Class<?> sourceClass) {
		return getProxy(sourceClass, null, null);
	}

	public final Executable getProxyExecutor(Executable executor) {
		return getProxyExecutor(executor, null);
	}

	public Executable getProxyExecutor(Executable executor, @Nullable ExecutionInterceptor executionInterceptor) {
		// 代理一个拦截器
		return null;
	}

	@Override
	public String toString() {
		return id;
	}
}
