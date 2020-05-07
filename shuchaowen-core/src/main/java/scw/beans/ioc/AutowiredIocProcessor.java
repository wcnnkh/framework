package scw.beans.ioc;

import scw.beans.BeanFactory;
import scw.beans.annotation.Autowired;
import scw.mapper.FieldContext;
import scw.util.value.property.PropertyFactory;

public class AutowiredIocProcessor extends DefaultFieldIocProcessor {

	public AutowiredIocProcessor(FieldContext fieldContext) {
		super(fieldContext);
	}

	public Object process(Object bean, BeanFactory beanFactory,
			PropertyFactory propertyFactory) throws Exception {
		Autowired s = getFieldContext().getField().getSetter()
				.getAnnotatedElement().getAnnotation(Autowired.class);
		if (s != null) {
			String name = s.value();
			if (name.length() == 0) {
				name = getFieldContext().getField().getSetter().getType()
						.getName();
			}

			existDefaultValueWarnLog(bean);
			Object instance = beanFactory.getInstance(name);
			getFieldContext().getField().getSetter().set(bean, instance);
		}
		return null;
	}
}
