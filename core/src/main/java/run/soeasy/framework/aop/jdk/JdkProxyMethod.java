package run.soeasy.framework.aop.jdk;

import java.lang.reflect.Method;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.aop.ProxyUtils;
import run.soeasy.framework.core.invoke.reflect.ReflectionMethod;

@Getter
public class JdkProxyMethod extends ReflectionMethod {
	private static final long serialVersionUID = 1L;

	public JdkProxyMethod(Method method) {
		super(method);
	}

	@Override
	public Object invoke(Object target, @NonNull Class<?>[] parameterTypes, @NonNull Object... args) throws Throwable {
		return ProxyUtils.invokeIgnoreMethod(target, getSource(), args);
	}

}
