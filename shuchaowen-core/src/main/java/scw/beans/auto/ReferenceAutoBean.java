package scw.beans.auto;

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

	public <T> T create() {
		return beanFactory.getInstance(reference);
	}

	public <T> T create(Object... params) {
		return beanFactory.getInstance(reference, params);
	}

	public <T> T create(Class<?>[] parameterTypes, Object... params) {
		return beanFactory.getInstance(reference, parameterTypes, params);
	}

	public String getId() {
		return beanFactory.getDefinition(reference).getId();
	}

	public boolean isSingleton() {
		return beanFactory.isSingleton(reference);
	}
}
