package scw.beans.ioc;

import scw.beans.BeanFactory;
import scw.beans.annotation.Config;
import scw.mapper.Field;
import scw.util.value.property.PropertyFactory;

public class ConfigIocProcessor extends DefaultFieldIocProcessor {

	public ConfigIocProcessor(Field field) {
		super(field);
	}

	public Object process(Object bean, BeanFactory beanFactory,
			PropertyFactory propertyFactory) throws Exception {
		Config config = getField().getSetter()
				.getAnnotatedElement().getAnnotation(Config.class);
		if (config != null) {
			Object value = null;
			existDefaultValueWarnLog(bean);
			value = beanFactory.getInstance(config.parse()).parse(beanFactory,
					propertyFactory, getField(), config.value(),
					config.charset());
			getField().getSetter().set(bean, value);
		}
		return null;
	}
}