package run.soeasy.framework.core.execution.aop.jdk;

import java.lang.reflect.Method;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.execution.aop.ProxyUtils;
import run.soeasy.framework.core.execution.reflect.ReflectionMethod;

@Getter
public class JdkProxyExecutor extends ReflectionMethod {

	public JdkProxyExecutor(Method method) {
		super(method);
	}

	@Override
	public Object invoke(Object target, @NonNull Object... args) throws Throwable {
		return ProxyUtils.invokeIgnoreMethod(target, getSource(), args);
	}
}
