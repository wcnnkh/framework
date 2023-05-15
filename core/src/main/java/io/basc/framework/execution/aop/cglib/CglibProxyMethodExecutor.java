package io.basc.framework.execution.aop.cglib;

import java.lang.reflect.Method;

import io.basc.framework.execution.AbstractMethodExecutor;
import io.basc.framework.execution.ExecutionException;
import io.basc.framework.util.Elements;
import io.basc.framework.value.Value;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.sf.cglib.proxy.MethodProxy;

@Data
@EqualsAndHashCode(callSuper = true)
public class CglibProxyMethodExecutor extends AbstractMethodExecutor {
	private final MethodProxy methodProxy;
	private final Object proxy;

	public CglibProxyMethodExecutor(Object proxy, Method method, MethodProxy methodProxy) {
		super(method);
		this.methodProxy = methodProxy;
		this.proxy = proxy;
	}

	@Override
	public Object execute(Elements<? extends Value> args) {
		try {
			return methodProxy.invokeSuper(proxy, args.map((e) -> e.getSource()).toArray());
		} catch (Throwable e) {
			throw new ExecutionException(getMethod().toString(), e);
		}
	}
}
