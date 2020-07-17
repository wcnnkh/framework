package scw.beans.ioc;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.annotation.Autowired;
import scw.lang.NotSupportedException;
import scw.mapper.Field;
import scw.value.property.PropertyFactory;

public class AutowiredIocProcessor extends DefaultFieldIocProcessor {

	public AutowiredIocProcessor(Field field) {
		super(field);
	}

	public void process(BeanDefinition beanDefinition, Object bean, BeanFactory beanFactory,
			PropertyFactory propertyFactory) throws Exception {
		Autowired s = getField().getSetter()
				.getAnnotatedElement().getAnnotation(Autowired.class);
		if (s != null) {
			String name = s.value();
			if (name.length() == 0) {
				name = getField().getSetter().getType()
						.getName();
			}

			checkField(bean);

			if(s.required()){//是否是强制依赖
				if(!beanFactory.isInstance(name)){
					throw new NotSupportedException(name);
				}
				

				getField().getSetter().set(bean, beanFactory.getInstance(name));
			}else{
				if(beanFactory.isInstance(name)){
					getField().getSetter().set(bean, beanFactory.getInstance(name));
				}
			}
		}
	}
}
