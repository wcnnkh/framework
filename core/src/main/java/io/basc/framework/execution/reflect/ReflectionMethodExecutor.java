package io.basc.framework.execution.reflect;

import java.lang.reflect.Method;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.execution.MethodExecutor;
import io.basc.framework.execution.aop.Aop;
import io.basc.framework.execution.aop.ExecutionInterceptor;
import io.basc.framework.execution.aop.Proxy;
import io.basc.framework.util.element.Elements;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReflectionMethodExecutor extends ReflectionExecutor<Method> implements MethodExecutor {
	private Aop aop;
	private Class<?>[] aopInterfaces;
	private ExecutionInterceptor executionInterceptor;
	private final TypeDescriptor targetTypeDescriptor;
	private Object target;

	public ReflectionMethodExecutor(Method executable, TypeDescriptor targetTypeDescriptor) {
		super(executable);
		this.targetTypeDescriptor = targetTypeDescriptor;
	}

	@Override
	public Object execute(Object target, Elements<Object> args) throws Throwable {
		Object value = ReflectionUtils.invoke(getExecutable(), target, args);
		if (aop == null) {
			return value;
		}
		Proxy proxy = aop.getProxy(getExecutable().getReturnType(), value);
		return proxy.execute();
	}

	@Override
	public final Object execute(Elements<Object> args) throws Throwable {
		return execute(getTarget(), args);
	}

	@Override
	public String getName() {
		return getExecutable().getName();
	}
}
