package io.basc.framework.execution.reflect;

import java.lang.reflect.Constructor;

import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.execution.Executor;
import io.basc.framework.execution.aop.Aop;
import io.basc.framework.execution.aop.ExecutionInterceptor;
import io.basc.framework.execution.aop.Proxy;
import io.basc.framework.util.element.Elements;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReflectionConstructor extends ReflectionExecutable<Constructor<?>> implements Executor {
	private Aop aop;
	private Class<?>[] aopInterfaces;
	private ExecutionInterceptor executionInterceptor;

	public ReflectionConstructor(Constructor<?> target) {
		super(target);
	}

	@Override
	public Object execute(Object[] args) throws Throwable {
		if (aop == null) {
			return ReflectionUtils.newInstance(getExecutable(), args);
		} else {
			Proxy proxy = aop.getProxy(getReturnTypeDescriptor().getType(), aopInterfaces, executionInterceptor);
			Class<?>[] types = Elements.forArray(getParameterDescriptors()).map((e) -> e.getTypeDescriptor().getType())
					.toArray(Class[]::new);
			return proxy.execute(types, args);
		}
	}

}
