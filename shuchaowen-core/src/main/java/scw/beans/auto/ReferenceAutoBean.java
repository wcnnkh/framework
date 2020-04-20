package scw.beans.auto;

import java.lang.reflect.AnnotatedElement;

import scw.beans.BeanFactory;

/**
 * 引用一个
 * 
 * @author shuchaowen
 *
 */
public class ReferenceAutoBean implements AutoBean {
	private String reference;
	private BeanFactory beanFactory;

	public ReferenceAutoBean(BeanFactory beanFactory, String reference) {
		this.beanFactory = beanFactory;
		this.reference = reference;
	}

	public boolean isReference() {
		return true;
	}

	public Class<?> getTargetClass() {
		return null;
	}

	public boolean isInstance() {
		return beanFactory.getDefinition(reference).isInstance();
	}

	public Object create() throws Exception{
		return beanFactory.getInstance(reference);
	}

	public Object create(Object... params) throws Exception{
		return beanFactory.getInstance(reference, params);
	}

	public Object create(Class<?>[] parameterTypes, Object... params) throws Exception{
		return beanFactory.getInstance(reference, parameterTypes, params);
	}

	public String getId() {
		return beanFactory.getDefinition(reference).getId();
	}

	public boolean isSingleton() {
		return beanFactory.isSingleton(reference);
	}

	public AnnotatedElement getAnnotatedElement() {
		return beanFactory.getDefinition(reference).getAnnotatedElement();
	}

	public void init(Object instance) throws Exception {
	}
}
