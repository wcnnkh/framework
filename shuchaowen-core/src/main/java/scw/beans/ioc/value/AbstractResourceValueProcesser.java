package scw.beans.ioc.value;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.annotation.Value;
import scw.io.event.ObservableResource;
import scw.io.event.ObservableResourceEvent;
import scw.io.event.ObservableResourceEventListener;
import scw.mapper.Field;
import scw.value.property.PropertyFactory;

public abstract class AbstractResourceValueProcesser<R> extends AbstractValueProcesser {

	@Override
	protected void processInteranl(final BeanDefinition beanDefinition, final BeanFactory beanFactory,
			final PropertyFactory propertyFactory, final Object bean, final Field field, final Value value,
			final String name, final String charsetName) {
		ObservableResource<R> res = getObservableResource(beanDefinition, beanFactory, propertyFactory, bean, field,
				value, name, charsetName);
		if (res.getResource() == null) {
			logger.warn("Nonexistent resources name:{}, field={}", name, field);
		} else {
			Object v = parse(beanDefinition, beanFactory, propertyFactory, bean, field, value, name, charsetName,
					res.getResource());
			if (v == null) {
				logger.warn("Value is a null! name={}, field={}", name, field);
			} else {
				field.getSetter().set(bean, v);
			}
		}

		if (isRegisterListener(beanDefinition, field, value)) {
			res.registerListener(new ObservableResourceEventListener<R>() {

				public void onEvent(ObservableResourceEvent<R> event) {
					R res = event.getSource();
					Object v = null;
					if (res == null) {
						logger.warn("Event: nonexistent resources name:{}, field={}", name, field);
					} else {
						v = parse(beanDefinition, beanFactory, propertyFactory, bean, field, value, name, charsetName,
								event.getSource());
						if (v == null) {
							logger.warn("Event: value is a null! name={}, field={}", name, field);
						}
					}

					field.getSetter().set(bean, v);
				}
			});
		}
	}

	protected abstract ObservableResource<R> getObservableResource(BeanDefinition beanDefinition,
			BeanFactory beanFactory, PropertyFactory propertyFactory, Object bean, Field field, Value value,
			String name, String charsetName);

	protected abstract Object parse(BeanDefinition beanDefinition, BeanFactory beanFactory,
			PropertyFactory propertyFactory, Object bean, Field field, Value value, String name, String charsetName,
			R resource);
}
