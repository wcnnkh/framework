package io.basc.framework.execution.aop.cglib;

import java.lang.reflect.Method;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.reflect.MethodExecutor;
import io.basc.framework.util.element.Elements;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.sf.cglib.proxy.MethodProxy;

@Data
@EqualsAndHashCode(callSuper = true)
public class CglibProxyExecutor extends MethodExecutor {
	private final MethodProxy methodProxy;

	public CglibProxyExecutor(TypeDescriptor source, Method method, Object proxy, MethodProxy methodProxy) {
		super(source, method, proxy);
		this.methodProxy = methodProxy;
	}

	@Override
	public Object execute(Elements<? extends Object> args) throws Throwable {
		return methodProxy.invokeSuper(getTarget(), args.toArray());
	}
}
