package scw.beans.definition.builder;

import java.lang.reflect.Field;
import java.util.LinkedList;

import scw.beans.BeanFactory;
import scw.beans.BeanMethod;
import scw.beans.BeanUtils;
import scw.core.instance.AbstractInstanceBuilder;
import scw.core.reflect.DefaultFieldDefinition;
import scw.core.reflect.FieldDefinition;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.util.value.property.PropertyFactory;

public abstract class AbstractBeanBuilder extends
		AbstractInstanceBuilder<Object> implements BeanBuilder {
	protected final Logger logger = LoggerUtils.getLogger(getClass());

	protected final BeanFactory beanFactory;
	protected final PropertyFactory propertyFactory;
	protected final LinkedList<String> filterNames = new LinkedList<String>();
	protected final LinkedList<BeanMethod> initMethods = new LinkedList<BeanMethod>();
	protected final LinkedList<BeanMethod> destroyMethods = new LinkedList<BeanMethod>();

	public AbstractBeanBuilder(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> targetClass) {
		super(targetClass);
		this.propertyFactory = propertyFactory;
		this.beanFactory = beanFactory;
	}

	protected FieldDefinition createFieldDefinition(Class<?> clazz, Field field) {
		return new DefaultFieldDefinition(clazz, field, false, false, true);
	}

	protected boolean isProxy() {
		return BeanUtils.isProxy(getTargetClass(), getTargetClass()) ? true
				: filterNames.isEmpty();
	}

	public void init(Object instance) throws Exception {
		for (BeanMethod beanMethod : initMethods) {
			beanMethod.invoke(instance, beanFactory, propertyFactory);
		}
	}

	public void destroy(Object instance) throws Exception {
		for (BeanMethod method : destroyMethods) {
			method.invoke(instance, beanFactory, propertyFactory);
		}
	}
}
