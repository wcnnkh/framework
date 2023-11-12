package io.basc.framework.execution.aop.cglib;

import java.lang.reflect.Method;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.reflect.ReflectionMethodExecutor;
import io.basc.framework.util.element.Elements;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.sf.cglib.proxy.MethodProxy;

@Data
@EqualsAndHashCode(callSuper = true)
public class CglibProxyExecutor extends ReflectionMethodExecutor {
	private final MethodProxy methodProxy;

	public CglibProxyExecutor(TypeDescriptor source, Method method, MethodProxy methodProxy) {
		super(method, source);
		this.methodProxy = methodProxy;
	}

	@Override
	public Object execute(Object target, Elements<Object> args) throws Throwable {
		return methodProxy.invokeSuper(target, args.toArray());
	}
}
