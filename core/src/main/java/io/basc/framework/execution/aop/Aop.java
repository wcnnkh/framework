package io.basc.framework.execution.aop;

import io.basc.framework.execution.Executor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.XUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Aop extends ProxyFactoryRegistry {
	private final String id;
	private final ExecutionInterceptorRegistry executionInterceptorRegistry = new ExecutionInterceptorRegistry();

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
	public Proxy getProxy(Class<?> sourceClass, @Nullable Class<?>[] interfaces,
			@Nullable ExecutionInterceptor executionInterceptor) {
		Assert.requiredArgument(sourceClass != null, "sourceClass");
		DelegatedObjectExecutionInterceptor delegatedObjectExecutionInterceptor = new DelegatedObjectExecutionInterceptor(
				this.id);
		Elements<? extends ExecutionInterceptor> executionInterceptors;
		if (executionInterceptor == null) {
			executionInterceptors = Elements.forArray(delegatedObjectExecutionInterceptor,
					getExecutionInterceptorRegistry());
		} else {
			executionInterceptors = Elements.forArray(delegatedObjectExecutionInterceptor,
					getExecutionInterceptorRegistry(), executionInterceptor);
		}
		ExecutionInterceptor useExecutionInterceptor = new ExecutionInterceptors(executionInterceptors);

		Class<?>[] useInterfaces = new Class<?>[] { DelegatedObject.class };
		if (interfaces != null) {
			useInterfaces = ArrayUtils.merge(useInterfaces, interfaces);
		}
		return super.getProxy(sourceClass, useInterfaces, useExecutionInterceptor);
	}

	public final Proxy getProxy(Class<?> sourceClass) {
		return getProxy(sourceClass, null, null);
	}

	public final Executor getProxyExecutor(Executor executor) {
		return getProxyExecutor(executor, null);
	}

	public Executor getProxyExecutor(Executor executor, @Nullable ExecutionInterceptor executionInterceptor) {
		Assert.requiredArgument(executor != null, "executor");
		DelegatedObjectExecutionInterceptor delegatedObjectExecutionInterceptor = new DelegatedObjectExecutionInterceptor(
				this.id);
		Elements<? extends ExecutionInterceptor> executionInterceptors;
		if (executionInterceptor == null) {
			executionInterceptors = Elements.forArray(delegatedObjectExecutionInterceptor,
					getExecutionInterceptorRegistry());
		} else {
			executionInterceptors = Elements.forArray(delegatedObjectExecutionInterceptor,
					getExecutionInterceptorRegistry(), executionInterceptor);
		}
		ExecutionInterceptor useExecutionInterceptor = new ExecutionInterceptors(executionInterceptors);
		return new InterceptableExecutor(executor, useExecutionInterceptor);
	}

	public final <T> Proxy getProxy(Class<? extends T> sourceClass, T source) {
		return getProxy(sourceClass, source, null, null);
	}

	public <T> Proxy getProxy(Class<? extends T> sourceClass, T source, @Nullable Class<?>[] interfaces,
			@Nullable ExecutionInterceptor executionInterceptor) {
		Assert.requiredArgument(sourceClass != null, "sourceClass");
		SwitchableTargetExecutionInterceptor switchableTargetExecutionInterceptor = new SwitchableTargetExecutionInterceptor(
				source);
		Elements<? extends ExecutionInterceptor> executionInterceptors;
		if (executionInterceptor == null) {
			executionInterceptors = Elements.forArray(switchableTargetExecutionInterceptor);
		} else {
			executionInterceptors = Elements.forArray(switchableTargetExecutionInterceptor, executionInterceptor);
		}
		ExecutionInterceptor useExecutionInterceptor = new ExecutionInterceptors(executionInterceptors);
		return getProxy(sourceClass, interfaces, useExecutionInterceptor);
	}

	@Override
	public String toString() {
		return id;
	}
}
