package scw.beans.ioc;

import scw.beans.BeanFactory;
import scw.beans.annotation.Config;
import scw.core.reflect.FieldDefinition;
import scw.util.value.property.PropertyFactory;

public class ConfigIocProcessor extends DefaultFieldIocProcessor {

	public ConfigIocProcessor(FieldDefinition fieldDefinition) {
		super(fieldDefinition);
	}

	public Object process(Object bean, BeanFactory beanFactory,
			PropertyFactory propertyFactory) throws Exception {
		Config config = getFieldDefinition().getAnnotatedElement()
				.getAnnotation(Config.class);
		if (config != null) {
			Object value = null;
			try {
				existDefaultValueWarnLog(bean);
				value = beanFactory.getInstance(config.parse()).parse(
						beanFactory, propertyFactory, getFieldDefinition(),
						config.value(), config.charset());
				return getFieldDefinition().set(bean, value);
			} catch (Exception e) {
				logger.error("clz="
						+ getFieldDefinition().getDeclaringClass().getName()
						+ ",fieldName="
						+ getFieldDefinition().getField().getName());
				throw e;
			}
		}
		return null;
	}
}