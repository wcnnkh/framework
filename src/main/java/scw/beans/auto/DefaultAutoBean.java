package scw.beans.auto;

import java.lang.reflect.Constructor;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.core.cglib.proxy.Enhancer;
import scw.core.exception.NotFoundException;
import scw.core.instance.InstanceUtils;
import scw.core.reflect.ReflectUtils;

public class DefaultAutoBean implements AutoBean {
	protected final BeanFactory beanFactory;
	protected final Class<?> type;

	public DefaultAutoBean(BeanFactory beanFactory, Class<?> type) {
		this.beanFactory = beanFactory;
		this.type = type;
	}

	public Object create(AutoBeanConfig config) throws Exception {
		if (config.isProxy()) {
			Enhancer enhancer = BeanUtils.createEnhancer(type, beanFactory,
					config.getFilters());
			return enhancer.create();
		} else {
			return InstanceUtils.getInstance(type);
		}
	}

	public Object create(AutoBeanConfig config, Object... params)
			throws Exception {
		Constructor<?> constructor = ReflectUtils.findConstructorByParameters(
				type, true, params);
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
		// TODO Auto-generated method stub
		return null;
	}

}
