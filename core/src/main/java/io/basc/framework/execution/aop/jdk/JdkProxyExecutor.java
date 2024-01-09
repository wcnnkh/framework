package io.basc.framework.execution.aop.jdk;

import java.lang.reflect.Method;

import io.basc.framework.execution.aop.ProxyUtils;
import io.basc.framework.execution.reflect.ReflectionMethod;
import io.basc.framework.util.element.Elements;
import lombok.Getter;

@Getter
public class JdkProxyExecutor extends ReflectionMethod {

	public JdkProxyExecutor(Method method) {
		super(method);
	}

	@Override
	public Object execute(Object target, Elements<? extends Object> args) throws Throwable {
		return ProxyUtils.invokeIgnoreMethod(target, getMember(), args.toArray());
	}
}
