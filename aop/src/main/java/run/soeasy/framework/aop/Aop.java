package run.soeasy.framework.aop;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import run.soeasy.framework.aop.jdk.JdkProxyFactory;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.collection.ArrayUtils;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.execute.Execution;
import run.soeasy.framework.sequences.uuid.UUIDSequences;

@Data
@EqualsAndHashCode(callSuper = true)
public class Aop extends JdkProxyFactory {
	private static volatile Aop global;

	public static Aop global() {
		if (global == null) {
			synchronized (Aop.class) {
				if (global == null) {
					global = new Aop();
					global.configure();
				}
			}
		}
		return global;
	}

	private final String id;
	private final ExecutionInterceptorRegistry executionInterceptorRegistry = new ExecutionInterceptorRegistry();

	public Aop() {
		this.id = UUIDSequences.global().next();
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

	public final Execution getProxyFunction(@NonNull Execution function) {
		return getProxyFunction(function, null);
	}

	public Execution getProxyFunction(@NonNull Execution execution, ExecutionInterceptor executionInterceptor) {
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
		return new InterceptableExecution<>(execution, useExecutionInterceptor);
	}

	public final <T> Proxy getProxy(Class<? extends T> sourceClass, T source) {
		return getProxy(sourceClass, source, null, null);
	}

	public <T> Proxy getProxy(@NonNull Class<? extends T> sourceClass, T source, Class<?>[] interfaces,
			ExecutionInterceptor executionInterceptor) {
		SwitchableTargetInvocationInterceptor switchableTargetExecutionInterceptor = new SwitchableTargetInvocationInterceptor(
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
