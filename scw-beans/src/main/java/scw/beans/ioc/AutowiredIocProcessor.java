package scw.beans.ioc;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.BeansException;
import scw.beans.annotation.Autowired;
import scw.core.reflect.ReflectionUtils;
import scw.lang.NotSupportedException;
import scw.mapper.Field;

public class AutowiredIocProcessor extends AbstractFieldIocProcessor {

	public AutowiredIocProcessor(Field field) {
		super(field);
	}

	@Override
	protected void processInternal(BeanDefinition beanDefinition, Object bean, BeanFactory beanFactory) throws BeansException {
		Autowired s = getField().getSetter().getAnnotation(Autowired.class);
		if (s != null) {
			String name = s.value();
			if (name.length() == 0) {
				name = getField().getSetter().getType()
						.getName();
			}

			if(s.required()){//是否是强制依赖
				if(!beanFactory.isInstance(name)){
					throw new NotSupportedException(getField().getSetter().toString());
				}
				

				getField().getSetter().set(bean, beanFactory.getInstance(name));
			}else{
				if(!exists(bean, getField())) {
					//仅当字段不存在值时才注入
					if(beanFactory.isInstance(name)){
						getField().getSetter().set(bean, beanFactory.getInstance(name));
					}
				}else {
					logger.debug("field already default value, field [{}]", getField().toString());
				}
			}
		}
	}
	
	private static boolean exists(Object instance, Field field) {
		java.lang.reflect.Field refField = null;
		if(field.isSupportGetter() && field.getGetter().getField() != null) {
			refField = field.getGetter().getField();
		}else if(field.isSupportSetter() && field.getSetter().getField() != null) {
			refField = field.getSetter().getField();
		}
		
		if(refField == null) {
			return false;
		}
		
		ReflectionUtils.makeAccessible(refField);
		return ReflectionUtils.getField(refField, instance) != null;
	}
}
