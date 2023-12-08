package io.basc.framework.execution.reflect;

import java.lang.reflect.Constructor;

import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.execution.aop.Aop;
import io.basc.framework.execution.aop.ExecutionInterceptor;
import io.basc.framework.execution.aop.Proxy;
import io.basc.framework.util.element.Elements;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReflectionConstructor extends ReflectionExecutor<Constructor<?>> {
	private Aop aop;
	private Class<?>[] aopInterfaces;
	private ExecutionInterceptor executionInterceptor;

	public ReflectionConstructor(Constructor<?> target) {
		super(target);
	}

	@Override
	public Object execute(Elements<? extends Object> args) throws Throwable {
		if (aop == null) {
			return ReflectionUtils.newInstance(getExecutable(), args);
		} else {
			Proxy proxy = aop.getProxy(getReturnTypeDescriptor().getType(), aopInterfaces, executionInterceptor);
			return proxy.execute(getParameterDescriptors().map((e) -> e.getTypeDescriptor().getType()), args);
		}
	}

}
