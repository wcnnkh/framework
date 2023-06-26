package io.basc.framework.beans.factory.config.support;

import io.basc.framework.beans.BeansException;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.MappingFactory;
import io.basc.framework.mapper.Setter;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Registration;
import io.basc.framework.util.StringUtils;
import io.basc.framework.value.DynamicPropertyFactory;
import io.basc.framework.value.PropertyFactory;
import io.basc.framework.value.Value;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class PropertyAutowiredBeanPostProcessor extends AutowiredBeanPostProcessor {
	private final PropertyFactory propertyFactory;
	private final BeanRegistrationManager beanRegistrationManager;

	public PropertyAutowiredBeanPostProcessor(MappingFactory mappingFactory, PropertyFactory propertyFactory,
			BeanRegistrationManager beanRegistrationManager) {
		super(mappingFactory);
		this.propertyFactory = propertyFactory;
		this.beanRegistrationManager = beanRegistrationManager;
	}

	/**
	 * 是否是单例
	 * 
	 * @param beanName
	 * @return
	 */
	protected abstract boolean isSingleton(String beanName);

	@Override
	public void postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (isSingleton(beanName) && beanRegistrationManager.isRegisted(beanName)) {
			return;
		}
		super.postProcessBeforeInitialization(bean, beanName);
	}

	@Override
	public void postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		super.postProcessAfterInitialization(bean, beanName);
		if (isSingleton(beanName)) {
			beanRegistrationManager.unregister(beanName);
		}
	}

	@Override
	protected void autowired(Object bean, String beanName, Field field) {
		Elements<String> setterNames = getPropertyNames(field);
		if (isSingleton(beanName)) {
			Registration registration = Registration.EMPTY;
			if (propertyFactory instanceof DynamicPropertyFactory) {
				DynamicPropertyFactory dynamicPropertyFactory = (DynamicPropertyFactory) propertyFactory;
				registration = dynamicPropertyFactory.getKeyEventRegistry().registerListener((event) -> {
					if (event.getSource().anyMatch(setterNames, StringUtils::equals)) {
						setProperty(bean, beanName, field, setterNames);
					}
				});
			}
			beanRegistrationManager.register(beanName, registration);
		}

		setProperty(bean, beanName, field, setterNames);
	}

	protected void setProperty(Object bean, String beanName, Field field, Elements<String> setterNames) {
		for (String name : setterNames) {
			if (propertyFactory.containsKey(name)) {
				Value value = propertyFactory.get(name);
				setProperty(bean, beanName, field, setterNames, name, value);
				break;
			}
		}
	}

	protected void setProperty(Object bean, String beanName, Field field, Elements<String> setterNames,
			String propertyName, Value value) {
		Setter setter = field.getSetters().first();
		Object setValue = value.getAsObject(setter.getTypeDescriptor());
		setter.set(bean, setValue);
	}

	/**
	 * 获取可以注入的属性别名
	 * 
	 * @param field
	 * @return
	 */
	protected abstract Elements<String> getPropertyNames(Field field);
}
