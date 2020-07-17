package scw.beans.ioc.value;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.annotation.Value;
import scw.event.EventListener;
import scw.mapper.Field;
import scw.value.property.PropertyEvent;
import scw.value.property.PropertyFactory;

public class DefaultValueProcess extends AbstractValueProcesser {

	@Override
	protected void processInteranl(BeanDefinition beanDefinition, BeanFactory beanFactory,
			PropertyFactory propertyFactory, final Object bean, final Field field, Value value, String name,
			String charsetName) {
		scw.value.Value v = propertyFactory.get(name);
		field.getSetter().set(bean, v == null ? null : v.getAsObject(field.getSetter().getGenericType()));
		if (isRegisterListener(beanDefinition, field)) {
			propertyFactory.registerListener(name, new EventListener<PropertyEvent>() {

				public void onEvent(PropertyEvent event) {
					field.getSetter().set(bean, event.getValue() == null ? null
							: event.getValue().getAsObject(field.getSetter().getGenericType()));
				}
			});
		}
	}
}
