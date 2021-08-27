package io.basc.framework.beans.ioc;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.beans.BeanFactory;
import io.basc.framework.beans.BeansException;
import io.basc.framework.mapper.Field;

public abstract class AbstractFieldIocProcessor extends AbstractIocProcessor {
	private final Field field;

	public AbstractFieldIocProcessor(Field field) {
		this.field = field;
	}

	public Field getField() {
		return field;
	}
	
	public void process(BeanDefinition beanDefinition, Object bean, BeanFactory beanFactory) throws BeansException {
		if(field == null){
			return ;
		}
		
		if(!acceptModifiers(beanDefinition, bean, field.getSetter().getModifiers())){
			return ;
		}
		
		checkField(bean, getField());
		processInternal(beanDefinition, bean, beanFactory);
	}
	
	protected abstract void processInternal(BeanDefinition beanDefinition, Object bean, BeanFactory beanFactory) throws BeansException;
}
