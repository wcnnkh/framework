package io.basc.framework.exec.aop.jdk;

import java.lang.reflect.Method;

import io.basc.framework.exec.AbstractMethodExecutor;
import io.basc.framework.exec.aop.ProxyUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.value.Value;
import lombok.Getter;

@Getter
public class JdkProxyMethodExecutor extends AbstractMethodExecutor {
	private final Object proxy;

	public JdkProxyMethodExecutor(Object proxy, Method method) {
		super(method);
		Assert.requiredArgument(proxy != null, "proxy");
		this.proxy = proxy;
	}

	@Override
	public Object execute(Elements<? extends Value> args) {
		// 如果filter中没有拦截这些方法，那么使用默认的调用
		return ProxyUtils.invokeIgnoreMethod(proxy, getMethod(), args);
	}

}
