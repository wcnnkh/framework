package scw.beans.auto;

import scw.beans.AbstractBeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.property.ValueWiredManager;
import scw.util.value.property.PropertyFactory;

public final class AutoBeanDefinition extends AbstractBeanDefinition {
	private final AutoBean autoBean;

	public AutoBeanDefinition(ValueWiredManager valueWiredManager,
			BeanFactory beanFactory, PropertyFactory propertyFactory,
			Class<?> type, AutoBean autoBean) throws Exception {
		super(valueWiredManager, beanFactory, propertyFactory, type);
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

	@SuppressWarnings("unchecked")
	public <T> T create() throws Exception {
		return (T) autoBean.create();
	}

	@SuppressWarnings("unchecked")
	public <T> T create(Object... params) throws Exception {
		return (T) autoBean.create(params);
	}

	@SuppressWarnings("unchecked")
	public <T> T create(Class<?>[] parameterTypes, Object... params)
			throws Exception {
		return (T) autoBean.create(parameterTypes, params);
	}

	public boolean isInstance() {
		return autoBean.isInstance();
	}

	public String[] getNames() {
		return null;
	}
}
