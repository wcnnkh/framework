package scw.beans;

import java.lang.reflect.AnnotatedElement;

import scw.lang.UnsupportedException;
import scw.util.value.property.PropertyFactory;

public abstract class AbstractInterfaceBeanDefinition extends AbstractBeanDefinition {

	public AbstractInterfaceBeanDefinition(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> type) {
		super(beanFactory, propertyFactory, type);
	}

	public boolean isInstance() {
		return true;
	}

	@Override
	public boolean isProxy() {
		return true;
	}

	public <T> T create(Object... params) throws Exception{
		throw new UnsupportedException(getTargetClass().getName());
	}

	public <T> T create(Class<?>[] parameterTypes, Object... params) throws Exception{
		throw new UnsupportedException(getTargetClass().getName());
	}

	public String[] getNames() {
		return null;
	}
	
	public AnnotatedElement getAnnotatedElement() {
		return getTargetClass();
	}
}
