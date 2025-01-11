package io.basc.framework.core.execution.aop;

import io.basc.framework.core.execution.Function;
import io.basc.framework.core.execution.aop.cglib.CglibProxyFactory;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.collections.ArrayUtils;
import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.sequences.uuid.UUIDSequences;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class Aop extends CglibProxyFactory {
	private static volatile Aop global;

	public static Aop global() {
		if (global == null) {
			synchronized (Aop.class) {
				if (global == null) {
					global = new Aop();
					global.doNativeConfigure();
				}
			}
		}
		return global;
	}

	private final String id;
	private final ExecutionInterceptorRegistry executionInterceptorRegistry = new ExecutionInterceptorRegistry();

	public Aop() {
		this.id = UUIDSequences.getInstance().next();
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
	public Proxy getProxy(@NonNull Class<?> sourceClass, Class<?>[] interfaces,
			ExecutionInterceptor executionInterceptor) {
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

	public final Proxy getProxy(@NonNull Class<?> sourceClass) {
		return getProxy(sourceClass, null, null);
	}

	public final Function getProxyFunction(@NonNull Function function) {
		return getProxyFunction(function, null);
	}

	public Function getProxyFunction(@NonNull Function function, ExecutionInterceptor executionInterceptor) {
		Assert.requiredArgument(function != null, "function");
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
		return new InterceptableFunction<>(function, useExecutionInterceptor);
	}

	public final <T> Proxy getProxy(Class<? extends T> sourceClass, T source) {
		return getProxy(sourceClass, source, null, null);
	}

	public <T> Proxy getProxy(@NonNull Class<? extends T> sourceClass, T source, Class<?>[] interfaces,
			ExecutionInterceptor executionInterceptor) {
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
