package scw.beans.ioc;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.BeansException;
import scw.mapper.Field;

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
