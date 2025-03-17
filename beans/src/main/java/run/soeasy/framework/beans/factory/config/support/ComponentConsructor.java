package run.soeasy.framework.beans.factory.config.support;

import java.lang.reflect.Constructor;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.beans.factory.BeanFactory;
import run.soeasy.framework.beans.factory.BeanFactoryAware;
import run.soeasy.framework.core.execution.aop.Aop;
import run.soeasy.framework.core.execution.aop.Proxy;
import run.soeasy.framework.core.execution.reflect.ReflectionConstructor;

@Getter
@Setter
public class ComponentConsructor extends ReflectionConstructor implements BeanFactoryAware {
	private BeanFactory beanFactory;
	private boolean enableAop;

	public ComponentConsructor(Constructor<?> target) {
		super(target);
	}

	@Override
	public Object execute(@NonNull Object... args) throws Throwable {
		if (enableAop) {
			Aop aop = new Aop();
			if (beanFactory != null) {
				aop.doConfigure(beanFactory);
				aop.getExecutionInterceptorRegistry().doConfigure(beanFactory);
			}
			Proxy proxy = aop.getProxy(getReturnTypeDescriptor().getType());
			proxy.execute(getParameterDescriptors().map((e) -> e.getTypeDescriptor().getType()).toArray(Class[]::new),
					args);
		}
		return super.execute(args);
	}
}
