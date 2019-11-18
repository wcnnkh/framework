package scw.beans.auto;

import scw.beans.AbstractBeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.property.ValueWiredManager;
import scw.core.PropertyFactory;
import scw.core.exception.BeansException;

public final class AutoBeanDefinition extends AbstractBeanDefinition {
	private final AutoBean autoBean;

	public AutoBeanDefinition(ValueWiredManager valueWiredManager, BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> type, AutoBean autoBean) throws Exception {
		super(valueWiredManager, beanFactory, propertyFactory, type);
		this.autoBean = autoBean;
		init();
	}

	public Class<?> getType() {
		return autoBean.getTargetClass() == null ? super.getType() : autoBean.getTargetClass();
	}

	public void autowrite(Object bean) throws Exception {
		if (autoBean.isReference()) {
			return;
		}
		super.autowrite(bean);
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
	public <T> T create() {
		try {
			return (T) autoBean.create();
		} catch (Exception e) {
			throw new BeansException(getId(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T create(Object... params) {
		try {
			return (T) autoBean.create(params);
		} catch (Exception e) {
			throw new BeansException(getId(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T create(Class<?>[] parameterTypes, Object... params) {
		try {
			return (T) autoBean.create(parameterTypes, params);
		} catch (Exception e) {
			throw new BeansException(getId(), e);
		}
	}

	public boolean isInstance() {
		return autoBean.isInstance();
	}

	public String[] getNames() {
		return null;
	}
}
