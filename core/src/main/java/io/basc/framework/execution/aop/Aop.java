package io.basc.framework.execution.aop;

import io.basc.framework.beans.factory.spi.SPI;
import io.basc.framework.execution.Function;
import io.basc.framework.execution.aop.cglib.CglibProxyFactory;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.XUtils;
import io.basc.framework.util.element.Elements;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Aop extends CglibProxyFactory {
	private static volatile Aop global;

	public static Aop global() {
		if (global == null) {
			synchronized (Aop.class) {
				if (global == null) {
					global = new Aop();
					global.configure(SPI.global());
				}
			}
		}
		return global;
	}

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

	public final Function getProxyFunction(Function function) {
		return getProxyFunction(function, null);
	}

	public Function getProxyFunction(Function function, @Nullable ExecutionInterceptor executionInterceptor) {
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
		return new InterceptableFunction(function, useExecutionInterceptor);
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
