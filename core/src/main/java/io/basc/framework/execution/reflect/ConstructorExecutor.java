package io.basc.framework.execution.reflect;

import java.lang.reflect.Constructor;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.execution.aop.Aop;
import io.basc.framework.execution.aop.ExecutionInterceptor;
import io.basc.framework.execution.aop.Proxy;
import io.basc.framework.util.Elements;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ConstructorExecutor extends ExecutableExecutor<Constructor<?>> {
	private Aop aop;
	private Class<?>[] aopInterfaces;
	private ExecutionInterceptor executionInterceptor;

	public ConstructorExecutor(TypeDescriptor source, Constructor<?> target) {
		super(source, target);
	}

	@Override
	public Object execute(Elements<? extends Object> args) throws Throwable {
		if (aop == null) {
			return ReflectionUtils.newInstance(getExecutable(), args.toArray());
		} else {
			Proxy proxy = aop.getProxy(getSource().getType(), aopInterfaces, executionInterceptor);
			Elements<? extends TypeDescriptor> types = getParameterDescriptors().map((e) -> e.getTypeDescriptor());
			return proxy.execute(types, args);
		}
	}

}
