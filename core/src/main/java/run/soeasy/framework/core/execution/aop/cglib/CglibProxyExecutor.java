package run.soeasy.framework.core.execution.aop.cglib;

import java.lang.reflect.Method;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import net.sf.cglib.proxy.MethodProxy;
import run.soeasy.framework.core.execution.reflect.ReflectionMethod;

@Data
@EqualsAndHashCode(callSuper = true)
public class CglibProxyExecutor extends ReflectionMethod {
	private final MethodProxy methodProxy;

	public CglibProxyExecutor(Method method, MethodProxy methodProxy) {
		super(method);
		this.methodProxy = methodProxy;
	}

	@Override
	public Object invoke(Object target, @NonNull Object... args) throws Throwable {
		return methodProxy.invokeSuper(target, args);
	}
}
