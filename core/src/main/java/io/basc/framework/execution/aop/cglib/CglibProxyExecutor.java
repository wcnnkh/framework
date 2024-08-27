package io.basc.framework.execution.aop.cglib;

import java.lang.reflect.Method;

import io.basc.framework.execution.reflect.ReflectionMethod;
import io.basc.framework.util.Elements;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.sf.cglib.proxy.MethodProxy;

@Data
@EqualsAndHashCode(callSuper = true)
public class CglibProxyExecutor extends ReflectionMethod {
	private final MethodProxy methodProxy;

	public CglibProxyExecutor(Method method, MethodProxy methodProxy) {
		super(method);
		this.methodProxy = methodProxy;
	}

	@Override
	public Object execute(Object target, Elements<? extends Object> args) throws Throwable {
		return methodProxy.invokeSuper(target, args.toArray());
	}
}
