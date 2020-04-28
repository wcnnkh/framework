package scw.beans;

import java.lang.reflect.Modifier;

import scw.aop.ProxyUtils;
import scw.beans.annotation.Bean;
import scw.core.instance.annotation.Configuration;
import scw.core.utils.XUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.util.value.property.PropertyFactory;

@Configuration(order = Integer.MIN_VALUE)
@Bean(proxy = false)
public class DefaultBeanLifeCycle implements BeanLifeCycle {
	protected final Logger logger = LoggerUtils.getLogger(getClass());

	protected BeanMetadata getBeanMetadata(Class<?> targetClass) {
		return new BeanMetadata(targetClass);
	}

	public void initBefore(BeanFactory beanFactory, PropertyFactory propertyFactory, BeanDefinition definition,
			Object instance) throws Exception {
		if (instance instanceof BeanFactoryAccessor) {
			((BeanFactoryAccessor) instance).setBeanFactory(beanFactory);
		}

		if (instance instanceof BeanDefinitionAware) {
			((BeanDefinitionAware) instance).setBeanDefinition(definition);
		}

		Class<?> instanceClass = ProxyUtils.getProxyAdapter().getUserClass(instance.getClass());
		for (BeanField field : getBeanMetadata(instanceClass).getAutowritedBeanFields()) {
			if(Modifier.isStatic(field.getFieldDefinition().getField().getModifiers())){
				continue;
			}
			field.wired(instance, beanFactory, propertyFactory);
		}
	}

	public void initAfter(BeanFactory beanFactory, PropertyFactory propertyFactory, BeanDefinition definition,
			Object instance) throws Exception {
		Class<?> instanceClass = ProxyUtils.getProxyAdapter().getUserClass(instance.getClass());
		for (BeanMethod beanMethod : getBeanMetadata(instanceClass).getInitMethods()) {
			if(Modifier.isStatic(beanMethod.getMethod().getModifiers())){
				continue;
			}
			beanMethod.invoke(instance, beanFactory, propertyFactory);
		}
		XUtils.init(instance);
	}

	public void destroyBefore(BeanFactory beanFactory, PropertyFactory propertyFactory, BeanDefinition definition,
			Object instance) throws Exception {
	}

	public void destroyAfter(BeanFactory beanFactory, PropertyFactory propertyFactory, BeanDefinition definition,
			Object instance) throws Exception {
		Class<?> instanceClass = ProxyUtils.getProxyAdapter().getUserClass(instance.getClass());
		for (BeanMethod beanMethod : getBeanMetadata(instanceClass).getDestroyMethods()) {
			if(Modifier.isStatic(beanMethod.getMethod().getModifiers())){
				continue;
			}
			beanMethod.invoke(instance, beanFactory, propertyFactory);
		}
		XUtils.destroy(instance);
	}
}
