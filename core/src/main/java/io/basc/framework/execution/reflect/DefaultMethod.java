package io.basc.framework.execution.reflect;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.execution.Method;
import io.basc.framework.execution.aop.Aop;
import io.basc.framework.execution.aop.ExecutionInterceptor;
import io.basc.framework.execution.aop.Proxy;
import io.basc.framework.util.element.Elements;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DefaultMethod extends ReflectionExecutable<java.lang.reflect.Method> implements Method {
	private Aop aop;
	private Class<?>[] aopInterfaces;
	private ExecutionInterceptor executionInterceptor;

	public DefaultMethod(TypeDescriptor source, java.lang.reflect.Method executable) {
		super(source, executable);
	}

	@Override
	public Object execute(Object target, Elements<? extends Object> args) throws Throwable {
		Object value = ReflectionUtils.invoke(getExecutable(), target, args.toArray());
		if (aop == null) {
			return value;
		}
		Proxy proxy = aop.getProxy(getExecutable().getReturnType(), value);
		return proxy.execute();
	}

}
