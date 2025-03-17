package run.soeasy.framework.core.execution.aop.cglib;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import net.sf.cglib.proxy.Enhancer;
import run.soeasy.framework.core.annotation.MergedAnnotations;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.execution.aop.ExecutionInterceptor;
import run.soeasy.framework.core.execution.aop.Proxy;
import run.soeasy.framework.util.reflect.ReflectionUtils;

@Data
@AllArgsConstructor
public class CglibProxy implements Proxy {
	private final TypeDescriptor returnTypeDescriptor;
	private final Enhancer enhancer;

	public CglibProxy(Class<?> targetClass, Class<?>[] interfaces, ExecutionInterceptor executionInterceptor) {
		this.returnTypeDescriptor = TypeDescriptor.valueOf(targetClass);
		this.enhancer = CglibUtils.createEnhancer(targetClass, interfaces);
		this.enhancer.setCallback(new ExecutionInterceptorToMethodInterceptor(executionInterceptor));
	}

	@Override
	public boolean canExecuted(@NonNull Class<?>... parameterTypes) {
		return ReflectionUtils.getDeclaredConstructor(returnTypeDescriptor.getType(), parameterTypes) != null;
	}

	@Override
	public Object execute(@NonNull Class<?>[] parameterTypes, @NonNull Object... args) {
		return enhancer.create(parameterTypes, args);
	}

	@Override
	public Object execute() {
		return enhancer.create();
	}

	@Override
	public MergedAnnotations getAnnotations() {
		return MergedAnnotations.from(returnTypeDescriptor);
	}
}