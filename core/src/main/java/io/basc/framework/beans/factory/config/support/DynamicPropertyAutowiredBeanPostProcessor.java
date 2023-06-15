package io.basc.framework.beans.factory.config.support;

import io.basc.framework.beans.BeansException;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.MappingFactory;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Registration;
import io.basc.framework.util.StringUtils;
import io.basc.framework.value.DynamicPropertyFactory;

public abstract class DynamicPropertyAutowiredBeanPostProcessor extends PropertyAutowiredBeanPostProcessor {
	private final BeanRegistrationManager beanRegistrationManager;

	public DynamicPropertyAutowiredBeanPostProcessor(MappingFactory mappingFactory,
			DynamicPropertyFactory dynamicPropertyFactory, BeanRegistrationManager beanRegistrationManager) {
		super(mappingFactory, dynamicPropertyFactory);
		this.beanRegistrationManager = beanRegistrationManager;
	}

	@Override
	public DynamicPropertyFactory getPropertyFactory() {
		return (DynamicPropertyFactory) super.getPropertyFactory();
	}

	/**
	 * 是否是单例
	 * 
	 * @param beanName
	 * @return
	 */
	protected abstract boolean isSingleton(String beanName);

	@Override
	protected void setProperty(Object bean, String beanName, Field field, Elements<String> setterNames) {
		if (isSingleton(beanName)) {
			Registration registration = getPropertyFactory().getKeyEventRegistry().registerListener((event) -> {
				if (event.getSource().anyMatch(setterNames, StringUtils::equals)) {
					super.setProperty(bean, beanName, field, setterNames);
				}
			});

			beanRegistrationManager.register(beanName, registration);
		}
		super.setProperty(bean, beanName, field, setterNames);
	}

	@Override
	public void postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (isSingleton(beanName) && beanRegistrationManager.isRegisted(beanName)) {
			return;
		}

		super.postProcessBeforeInitialization(bean, beanName);
	}

	@Override
	public void postProcessAfterDestory(Object bean, String beanName) throws BeansException {
		if (isSingleton(beanName)) {
			beanRegistrationManager.unregister(beanName);
		}
		super.postProcessAfterDestory(bean, beanName);
	}
}
