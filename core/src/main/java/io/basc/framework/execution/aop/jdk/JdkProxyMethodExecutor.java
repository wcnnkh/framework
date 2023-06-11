package io.basc.framework.execution.aop.jdk;

import java.lang.reflect.Method;

import io.basc.framework.execution.aop.ProxyUtils;
import io.basc.framework.execution.reflect.ExecutableMethod;
import io.basc.framework.util.Elements;
import lombok.Getter;

@Getter
public class JdkProxyMethodExecutor extends ExecutableMethod {

	public JdkProxyMethodExecutor(Object proxy, Method method) {
		super(proxy, method);
	}

	@Override
	public Object execute(Elements<? extends Object> args) {
		// 如果filter中没有拦截这些方法，那么使用默认的调用
		return ProxyUtils.invokeIgnoreMethod(getTarget(), getSource(), args);
	}

}
