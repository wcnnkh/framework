package scw.beans;

import java.lang.reflect.Constructor;

import scw.aop.Proxy;
import scw.beans.auto.AutoBean;
import scw.beans.auto.SimpleAutoBean;
import scw.lang.UnsupportedException;
import scw.util.value.property.PropertyFactory;

public final class ServiceBeanDefinition extends AbstractBeanDefinition {
	private AutoBean autoBean;
	private boolean instance = true;
	private String[] names;

	public ServiceBeanDefinition(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> type) {
		super(beanFactory, propertyFactory, type);
		init();
		if (type.isInterface()) {
			this.instance = true;
		} else {
			this.autoBean = new SimpleAutoBean(beanFactory, type,
					propertyFactory);
			this.instance = autoBean.isInstance();
		}
		this.names = BeanUtils.getServiceNames(getTargetClass());
	}

	public boolean isInstance() {
		return instance;
	}

	protected Proxy getProxy() {
		return BeanUtils.createProxy(beanFactory, getTargetClass(), null, null);
	}

	@Override
	public void init(Object bean) throws Exception {
		super.init(bean);
	}

	@Override
	public void destroy(Object bean) throws Exception {
		super.destroy(bean);
	}

	public Object create() throws Exception {
		if (!isInstance()) {
			throw new UnsupportedException(getTargetClass().toString());
		}

		if (getTargetClass().isInterface()) {
			return getProxy().create();
		}

		return autoBean.create();
	}

	@Override
	protected Object createInternal(Class<?> targetClass,
			Constructor<? extends Object> constructor, Object[] params)
			throws Exception {
		if (isProxy()) {
			return getProxy().create(constructor.getParameterTypes(), params);
		} else {
			return constructor.newInstance(params);
		}
	}

	public String[] getNames() {
		return names;
	}
}