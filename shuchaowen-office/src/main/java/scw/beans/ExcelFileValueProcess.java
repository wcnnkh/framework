package scw.beans;

import scw.beans.annotation.Value;
import scw.beans.ioc.value.AbstractObservableResourceValueProcesser;
import scw.io.Resource;
import scw.mapper.Field;
import scw.value.property.PropertyFactory;

public class ExcelFileValueProcess extends AbstractObservableResourceValueProcesser {

	@Override
	protected Object parse(BeanDefinition beanDefinition, BeanFactory beanFactory, PropertyFactory propertyFactory,
			Object bean, Field field, Value value, String name, String charsetName, Resource resource)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
