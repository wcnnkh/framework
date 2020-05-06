package scw.beans.ioc;

import scw.beans.BeanFactory;
import scw.beans.annotation.Config;
import scw.core.reflect.FieldContext;
import scw.util.value.property.PropertyFactory;

public class ConfigIocProcessor extends DefaultFieldIocProcessor {

	public ConfigIocProcessor(FieldContext fieldContext) {
		super(fieldContext);
	}

	public Object process(Object bean, BeanFactory beanFactory,
			PropertyFactory propertyFactory) throws Exception {
		Config config = getFieldContext().getField().getSetter().getAnnotatedElement()
				.getAnnotation(Config.class);
		if (config != null) {
			Object value = null;
			try {
				existDefaultValueWarnLog(bean);
				value = beanFactory.getInstance(config.parse()).parse(
						beanFactory, propertyFactory, getFieldContext(),
						config.value(), config.charset());
				getFieldContext().getField().getSetter().set(bean, value);
			} catch (Exception e) {
				logger.error("clz="
						+ getFieldContext().getDeclaringClass().getName()
						+ ",fieldName="
						+ getFieldContext().getField().getSetter().getName());
				throw e;
			}
		}
		return null;
	}
}