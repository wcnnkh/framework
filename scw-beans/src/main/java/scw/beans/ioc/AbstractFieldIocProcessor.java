package scw.beans.ioc;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.mapper.Field;
import scw.value.property.PropertyFactory;

public abstract class AbstractFieldIocProcessor extends AbstractIocProcessor {
	private final Field field;

	public AbstractFieldIocProcessor(Field field) {
		this.field = field;
	}

	public Field getField() {
		return field;
	}
	
	public void process(BeanDefinition beanDefinition, Object bean, BeanFactory beanFactory,
			PropertyFactory propertyFactory) throws Exception {
		if(field == null){
			return ;
		}
		
		if(!acceptModifiers(beanDefinition, bean, field.getSetter().getModifiers())){
			return ;
		}
		
		checkField(bean, getField());
		processInternal(beanDefinition, bean, beanFactory, propertyFactory);
	}
	
	protected abstract void processInternal(BeanDefinition beanDefinition, Object bean, BeanFactory beanFactory,
			PropertyFactory propertyFactory) throws Exception;
}
