package scw.beans.auto;

import java.lang.reflect.Constructor;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.core.cglib.proxy.Enhancer;
import scw.core.exception.NotFoundException;
import scw.core.reflect.ReflectUtils;

public abstract class AbstractAutoBean implements AutoBean {
	protected final BeanFactory beanFactory;
	protected final Class<?> type;

	public AbstractAutoBean(BeanFactory beanFactory, Class<?> type) {
		this.beanFactory = beanFactory;
		this.type = type;
	}

	protected abstract Class<?>[] getParameterTypes();

	protected abstract Object[] getParameters();

	public Object create(AutoBeanConfig config) throws Exception {
		Class<?>[] types = getParameterTypes();
		Object[] parameters = getParameters();
		return create(config, types == null ? new Class<?>[0] : types,
				parameters == null ? new Object[0] : parameters);
	}

	public Object create(AutoBeanConfig config, Object... params)
			throws Exception {
		Constructor<?> constructor = ReflectUtils.findConstructorByParameters(
				type, false, params);
		if (constructor == null) {
			throw new NotFoundException(type + "找不到指定的构造方法");
		}

		if (config.isProxy()) {
			Enhancer enhancer = BeanUtils.createEnhancer(type, beanFactory,
					config.getFilters());
			return enhancer.create(constructor.getParameterTypes(), params);
		} else {
			return constructor.newInstance(params);
		}
	}

	public Object create(AutoBeanConfig config, Class<?>[] parameterTypes,
			Object... params) throws Exception {
		Constructor<?> constructor = ReflectUtils.getConstructor(type, false,
				parameterTypes);
		if (constructor == null) {
			throw new NotFoundException(type + "找不到指定的构造方法");
		}

		if (config.isProxy()) {
			Enhancer enhancer = BeanUtils.createEnhancer(type, beanFactory,
					config.getFilters());
			return enhancer.create(constructor.getParameterTypes(), params);
		} else {
			return constructor.newInstance(params);
		}
	}
}
