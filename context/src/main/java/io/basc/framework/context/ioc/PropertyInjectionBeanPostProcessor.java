package io.basc.framework.context.ioc;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.config.BeanPostProcessor;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.Mapping;
import io.basc.framework.mapper.MappingFactory;
import io.basc.framework.mapper.Setter;
import io.basc.framework.util.Elements;
import io.basc.framework.value.PropertyFactory;
import io.basc.framework.value.Value;
import lombok.Data;

@Data
public abstract class PropertyInjectionBeanPostProcessor implements BeanPostProcessor {
	private PropertyFactory propertyFactory;
	private Object object;
	private final MappingFactory mappingFactory;

	@Override
	public void postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		Mapping<? extends Field> mapping = mappingFactory.getMapping(bean.getClass());
		for (Field field : mapping.getElements()) {
			if (canPropertyInject(field)) {
				setProperty(bean, beanName, field, getPropertyNames(field));
			}
		}
	}

	public void setProperty(Object bean, String beanName, Field field, Elements<String> setterNames) {
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
	 * 是否可以进行属性注入
	 * 
	 * @param field
	 * @return
	 */
	public abstract boolean canPropertyInject(Field field);

	/**
	 * 获取可以注入的属性别名
	 * 
	 * @param field
	 * @return
	 */
	public abstract Elements<String> getPropertyNames(Field field);
}
