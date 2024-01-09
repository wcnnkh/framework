package io.basc.framework.beans.factory.config.support;

import java.lang.reflect.Method;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.beans.factory.BeanFactoryAware;
import io.basc.framework.execution.aop.Aop;
import io.basc.framework.execution.aop.ExecutionInterceptor;
import io.basc.framework.execution.aop.Proxy;
import io.basc.framework.execution.aop.ProxyFactory;
import io.basc.framework.execution.reflect.ReflectionMethod;
import io.basc.framework.util.element.Elements;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigurationMethodExecutor extends ReflectionMethod implements BeanFactoryAware {
	private BeanFactory beanFactory;
	private boolean enableAop;

	public ConfigurationMethodExecutor(Method executable) {
		super(executable);
	}

	@Override
	public Object execute(Object target, Elements<? extends Object> args) throws Throwable {
		Object value = super.execute(target, args);
		if (enableAop) {
			Aop aop = new Aop();
			if (beanFactory != null) {
				aop.registerServiceLoader(beanFactory.getBeanProvider(ProxyFactory.class));
				aop.getExecutionInterceptorRegistry()
						.registerServiceLoader(beanFactory.getBeanProvider(ExecutionInterceptor.class));
			}
			Proxy proxy = aop.getProxy(getReturnTypeDescriptor().getType(), value);
			value = proxy.execute();
		}
		return value;
	}
}
