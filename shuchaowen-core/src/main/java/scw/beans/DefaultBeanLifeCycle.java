package scw.beans;

import scw.aop.ProxyUtils;
import scw.beans.annotation.Bean;
import scw.beans.ioc.Ioc;
import scw.core.instance.annotation.Configuration;
import scw.core.utils.XUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.util.value.property.PropertyFactory;

@Configuration(order = Integer.MIN_VALUE)
@Bean(proxy = false)
public class DefaultBeanLifeCycle implements BeanLifeCycle {
	protected final Logger logger = LoggerUtils.getLogger(getClass());

	protected Ioc getIoc(Class<?> targetClass) {
		return new Ioc(targetClass);
	}

	public void initBefore(BeanFactory beanFactory,
			PropertyFactory propertyFactory, BeanDefinition definition,
			Object instance) throws Exception {
		if (instance instanceof BeanFactoryAccessor) {
			((BeanFactoryAccessor) instance).setBeanFactory(beanFactory);
		}

		if (instance instanceof BeanDefinitionAware) {
			((BeanDefinitionAware) instance).setBeanDefinition(definition);
		}

		Class<?> instanceClass = ProxyUtils.getProxyFactory().getUserClass(
				instance.getClass());
		getIoc(instanceClass).getAutowired().process(instance, beanFactory,
				propertyFactory, false);
	}

	public void initAfter(BeanFactory beanFactory,
			PropertyFactory propertyFactory, BeanDefinition definition,
			Object instance) throws Exception {
		Class<?> instanceClass = ProxyUtils.getProxyFactory().getUserClass(
				instance.getClass());
		getIoc(instanceClass).getInit().process(instance, beanFactory,
				propertyFactory, false);
		XUtils.init(instance);
	}

	public void destroyBefore(BeanFactory beanFactory,
			PropertyFactory propertyFactory, BeanDefinition definition,
			Object instance) throws Exception {
	}

	public void destroyAfter(BeanFactory beanFactory,
			PropertyFactory propertyFactory, BeanDefinition definition,
			Object instance) throws Exception {
		Class<?> instanceClass = ProxyUtils.getProxyFactory().getUserClass(
				instance.getClass());
		getIoc(instanceClass).getDestroy().process(instance, beanFactory,
				propertyFactory, false);
		XUtils.destroy(instance);
	}
}
