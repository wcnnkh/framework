package io.basc.framework.execution.aop.jdk;

import java.lang.reflect.Method;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.aop.ProxyUtils;
import io.basc.framework.execution.reflect.ReflectionMethodExecutor;
import lombok.Getter;

@Getter
public class JdkProxyExecutor extends ReflectionMethodExecutor {

	public JdkProxyExecutor(TypeDescriptor source, Method method, Object proxy) {
		super(method, source, proxy);
	}

	@Override
	public Object execute(Object[] args) {
		// 如果filter中没有拦截这些方法，那么使用默认的调用
		return ProxyUtils.invokeIgnoreMethod(getTarget(), getExecutable(), args);
	}

}
