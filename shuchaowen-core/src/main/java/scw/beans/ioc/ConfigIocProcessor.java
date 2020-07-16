package scw.beans.ioc;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.annotation.Config;
import scw.mapper.Field;
import scw.value.property.PropertyFactory;

public class ConfigIocProcessor extends DefaultFieldIocProcessor {

	public ConfigIocProcessor(Field field) {
		super(field);
	}

	public void process(BeanDefinition beanDefinition, Object bean, BeanFactory beanFactory,
			PropertyFactory propertyFactory) throws Exception {
		Config config = getField().getSetter()
				.getAnnotatedElement().getAnnotation(Config.class);
		if (config != null) {
			Object value = null;
			checkField(bean);
			value = beanFactory.getInstance(config.parse()).parse(beanFactory,
					propertyFactory, getField(), config.value(),
					config.charset());
			getField().getSetter().set(bean, value);
		}
	}
}