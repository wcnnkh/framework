package io.basc.framework.beans.factory.config.support;

import java.lang.reflect.Method;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.beans.factory.BeanFactoryAware;
import io.basc.framework.core.execution.aop.Aop;
import io.basc.framework.core.execution.aop.Proxy;
import io.basc.framework.core.execution.reflect.ReflectionMethod;
import lombok.Getter;
import lombok.NonNull;
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
	public Object invoke(Object target, @NonNull Object... args) throws Throwable {
		Object value = super.invoke(target, args);
		if (enableAop) {
			Aop aop = new Aop();
			if (beanFactory != null) {
				aop.doConfigure(beanFactory);
				aop.getExecutionInterceptorRegistry().doConfigure(beanFactory);
			}
			Proxy proxy = aop.getProxy(getReturnTypeDescriptor().getType(), value);
			value = proxy.execute();
		}
		return value;
	}
}
