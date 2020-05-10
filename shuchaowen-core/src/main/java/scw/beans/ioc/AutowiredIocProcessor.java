package scw.beans.ioc;

import scw.beans.BeanFactory;
import scw.beans.annotation.Autowired;
import scw.mapper.Field;
import scw.util.value.property.PropertyFactory;

public class AutowiredIocProcessor extends DefaultFieldIocProcessor {

	public AutowiredIocProcessor(Field field) {
		super(field);
	}

	public Object process(Object bean, BeanFactory beanFactory,
			PropertyFactory propertyFactory) throws Exception {
		Autowired s = getField().getSetter()
				.getAnnotatedElement().getAnnotation(Autowired.class);
		if (s != null) {
			String name = s.value();
			if (name.length() == 0) {
				name = getField().getSetter().getType()
						.getName();
			}

			existDefaultValueWarnLog(bean);
			Object instance = beanFactory.getInstance(name);
			getField().getSetter().set(bean, instance);
		}
		return null;
	}
}
