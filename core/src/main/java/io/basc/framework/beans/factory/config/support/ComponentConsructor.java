package io.basc.framework.beans.factory.config.support;

import java.lang.reflect.Constructor;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.beans.factory.BeanFactoryAware;
import io.basc.framework.execution.aop.Aop;
import io.basc.framework.execution.aop.ExecutionInterceptor;
import io.basc.framework.execution.aop.Proxy;
import io.basc.framework.execution.aop.ProxyFactory;
import io.basc.framework.execution.reflect.ReflectionConstructor;
import io.basc.framework.util.element.Elements;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComponentConsructor extends ReflectionConstructor implements BeanFactoryAware {
	private BeanFactory beanFactory;
	private boolean enableAop;

	public ComponentConsructor(Constructor<?> target) {
		super(target);
	}

	@Override
	public Object execute(Elements<? extends Object> args) throws Throwable {
		if (enableAop) {
			Aop aop = new Aop();
			if (beanFactory != null) {
				aop.getServiceLoaderRegistry().register(beanFactory.getBeanProvider(ProxyFactory.class));
				aop.getExecutionInterceptorRegistry().getServiceLoaderRegistry()
						.register(beanFactory.getBeanProvider(ExecutionInterceptor.class));
			}
			Proxy proxy = aop.getProxy(getReturnTypeDescriptor().getType());
			return proxy.execute(getParameterDescriptors().map((e) -> e.getTypeDescriptor().getType()), args);
		}
		return super.execute(args);
	}

}
