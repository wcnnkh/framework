package io.basc.framework.execution.aop.cglib;

import java.lang.reflect.Method;

import io.basc.framework.execution.reflect.ExecutableMethod;
import io.basc.framework.util.Elements;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.sf.cglib.proxy.MethodProxy;

@Data
@EqualsAndHashCode(callSuper = true)
public class CglibProxyMethodExecutor extends ExecutableMethod {
	private final MethodProxy methodProxy;

	public CglibProxyMethodExecutor(Object proxy, Method method, MethodProxy methodProxy) {
		super(proxy, method);
		this.methodProxy = methodProxy;
	}

	@Override
	public Object execute(Elements<? extends Object> args) throws Throwable {
		return methodProxy.invokeSuper(getTarget(), args.toArray());
	}
}
