package scw.beans.auto;

import scw.beans.AbstractBeanDefinition;
import scw.beans.BeanFactory;
import scw.util.value.property.PropertyFactory;

public final class AutoBeanDefinition extends AbstractBeanDefinition {
	private final AutoBean autoBean;

	public AutoBeanDefinition(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> type, AutoBean autoBean)
			throws Exception {
		super(beanFactory, propertyFactory, type);
		this.autoBean = autoBean;
		init();
	}

	public Class<?> getTargetClass() {
		return autoBean.getTargetClass() == null ? super.getTargetClass()
				: autoBean.getTargetClass();
	}

	public void init(Object bean) throws Exception {
		if (autoBean.isReference()) {
			return;
		}
		super.init(bean);
	}

	public void destroy(Object bean) throws Exception {
		if (autoBean.isReference()) {
			return;
		}

		super.destroy(bean);
	}

	public Object create() throws Exception {
		return autoBean.create();
	}

	public Object create(Object... params) throws Exception {
		return autoBean.create(params);
	}

	public Object create(Class<?>[] parameterTypes, Object... params)
			throws Exception {
		return autoBean.create(parameterTypes, params);
	}

	public boolean isInstance() {
		return autoBean.isInstance();
	}

	public String[] getNames() {
		return null;
	}
}
