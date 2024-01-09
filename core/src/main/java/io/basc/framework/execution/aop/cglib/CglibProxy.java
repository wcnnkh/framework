package io.basc.framework.execution.aop.cglib;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.annotation.MergedAnnotations;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.execution.aop.ExecutionInterceptor;
import io.basc.framework.execution.aop.Proxy;
import io.basc.framework.util.element.Elements;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.sf.cglib.proxy.Enhancer;

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
	public boolean canExecuted(Elements<? extends Class<?>> parameterTypes) {
		return ReflectionUtils.getDeclaredConstructor(returnTypeDescriptor.getType(),
				parameterTypes.toArray(Class[]::new)) != null;
	}

	@Override
	public Object execute(Elements<? extends Class<?>> parameterTypes, Elements<? extends Object> args) {
		return enhancer.create(parameterTypes.toArray(Class[]::new), args.toArray());
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