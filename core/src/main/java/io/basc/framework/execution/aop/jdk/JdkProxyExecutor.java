package io.basc.framework.execution.aop.jdk;

import java.lang.reflect.Method;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.aop.ProxyUtils;
import io.basc.framework.execution.reflect.ReflectionMethodExecutor;
import io.basc.framework.util.element.Elements;
import lombok.Getter;

@Getter
public class JdkProxyExecutor extends ReflectionMethodExecutor {

	public JdkProxyExecutor(TypeDescriptor source, Method method) {
		super(method, source);
	}

	@Override
	public Object execute(Object target, Elements<Object> args) throws Throwable {
		return ProxyUtils.invokeIgnoreMethod(target, getExecutable(), args.toArray());
	}
}
