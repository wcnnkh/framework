package run.soeasy.framework.beans.factory.ioc;

import lombok.Data;
import run.soeasy.framework.beans.BeansException;
import run.soeasy.framework.beans.factory.BeanFactory;
import run.soeasy.framework.beans.factory.config.BeanPostProcessor;

@Data
public class DependencyInjectionBeanPostProcessor implements BeanPostProcessor {
	private final BeanFactory beanFactory;
	private final BeanLifecycleResolver beanLifecycleResolver;
	private final BeanPropertyResolver beanPropertyResolver;

	@Override
	public void postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

	}

	@Override
	public void postProcessBeforeDestroy(Object bean, String beanName) throws BeansException {

	}
}
