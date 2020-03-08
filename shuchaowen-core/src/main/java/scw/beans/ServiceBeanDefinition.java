package scw.beans;

import java.lang.reflect.Constructor;

import scw.aop.Proxy;
import scw.beans.auto.AutoBean;
import scw.beans.auto.SimpleAutoBean;
import scw.beans.property.ValueWiredManager;
import scw.core.PropertyFactory;
import scw.core.reflect.ReflectionUtils;
import scw.lang.NotFoundException;
import scw.lang.NotSupportException;

public final class ServiceBeanDefinition extends AbstractBeanDefinition {
	private AutoBean autoBean;
	private boolean instance = true;
	private String[] names;

	public ServiceBeanDefinition(ValueWiredManager valueWiredManager,
			BeanFactory beanFactory, PropertyFactory propertyFactory,
			Class<?> type) {
		super(valueWiredManager, beanFactory, propertyFactory, type);
		init();
		if (type.isInterface()) {
			this.instance = true;
		} else {
			this.autoBean = new SimpleAutoBean(beanFactory, type,
					propertyFactory);
			this.instance = autoBean.isInstance();
		}
		this.names = BeanUtils.getServiceNames(getType());
	}

	public boolean isInstance() {
		return instance;
	}

	protected Proxy getProxy() {
		return BeanUtils.createProxy(beanFactory, getType(), null, null);
	}

	@Override
	public void init(Object bean) throws Exception {
		if (autoBean != null) {
			autoBean.init(bean);
		}
		super.init(bean);
	}

	@Override
	public void destroy(Object bean) throws Exception {
		if (autoBean != null) {
			autoBean.destroy(bean);
		}
		super.destroy(bean);
	}

	@SuppressWarnings("unchecked")
	public <T> T create() throws Exception {
		if (!isInstance()) {
			throw new NotSupportException(getType().toString());
		}

		if (getType().isInterface()) {
			return (T) getProxy().create();
		}

		return (T) autoBean.create();
	}

	@SuppressWarnings("unchecked")
	public <T> T create(Object... params) throws Exception {
		Constructor<T> constructor = (Constructor<T>) ReflectionUtils
				.findConstructorByParameters(getType(), true, params);
		if (constructor == null) {
			throw new NotFoundException(getId() + "找不到指定的构造方法");
		}

		Object bean;
		if (isProxy()) {
			return (T) getProxy().create(constructor.getParameterTypes(),
					params);
		} else {
			bean = constructor.newInstance(params);
		}
		return (T) bean;
	}

	@SuppressWarnings("unchecked")
	public <T> T create(Class<?>[] parameterTypes, Object... params)
			throws Exception {
		Constructor<?> constructor = ReflectionUtils.getConstructor(getType(),
				false, parameterTypes);
		if (constructor == null) {
			throw new NotFoundException(getId() + "找不到指定的构造方法");
		}

		Object bean;
		if (isProxy()) {
			return (T) getProxy().create(constructor.getParameterTypes(),
					params);
		} else {
			bean = constructor.newInstance(params);
		}
		return (T) bean;
	}

	public String[] getNames() {
		return names;
	}
}