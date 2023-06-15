package io.basc.framework.beans.factory.config.support;

import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.MappingFactory;
import io.basc.framework.mapper.Setter;
import io.basc.framework.util.Elements;
import io.basc.framework.value.PropertyFactory;
import io.basc.framework.value.Value;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class PropertyAutowiredBeanPostProcessor extends AutowiredBeanPostProcessor {
	private final PropertyFactory propertyFactory;

	public PropertyAutowiredBeanPostProcessor(MappingFactory mappingFactory, PropertyFactory propertyFactory) {
		super(mappingFactory);
		this.propertyFactory = propertyFactory;
	}

	@Override
	protected void autowired(Object bean, String beanName, Field field) {
		Elements<String> setterNames = getPropertyNames(field);
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
