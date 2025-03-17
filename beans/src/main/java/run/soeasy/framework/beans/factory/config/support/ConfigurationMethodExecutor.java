package run.soeasy.framework.beans.factory.config.support;

import java.lang.reflect.Method;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.beans.factory.BeanFactory;
import run.soeasy.framework.beans.factory.BeanFactoryAware;
import run.soeasy.framework.core.execution.aop.Aop;
import run.soeasy.framework.core.execution.aop.Proxy;
import run.soeasy.framework.core.execution.reflect.ReflectionMethod;

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
