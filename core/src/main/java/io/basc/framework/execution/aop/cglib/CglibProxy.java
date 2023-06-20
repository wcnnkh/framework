package io.basc.framework.execution.aop.cglib;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.execution.aop.ExecutionInterceptor;
import io.basc.framework.execution.aop.Proxy;
import io.basc.framework.util.Elements;
import lombok.Data;
import net.sf.cglib.proxy.Enhancer;

@Data
public class CglibProxy implements Proxy {
	private final TypeDescriptor source;
	private final Enhancer enhancer;

	public CglibProxy(TypeDescriptor source, Class<?>[] interfaces, ExecutionInterceptor executionInterceptor) {
		this.source = source;
		this.enhancer = CglibUtils.createEnhancer(source.getType(), interfaces);
		this.enhancer.setCallback(new ExecutionInterceptorToMethodInterceptor(source, executionInterceptor));
	}

	@Override
	public TypeDescriptor getReturnType() {
		return source;
	}

	@Override
	public boolean isExecuted(Elements<? extends TypeDescriptor> types) {
		return ReflectionUtils.getDeclaredConstructor(source.getType(), types.toArray(new Class[0])) != null;
	}

	@Override
	public Object execute(Elements<? extends TypeDescriptor> types, Elements<? extends Object> args) throws Throwable {
		return enhancer.create(types.toArray(new Class[0]), args.toArray());
	}
}