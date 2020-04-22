package scw.beans;

import java.lang.reflect.Constructor;

import scw.core.instance.ConstructorBuilder;
import scw.core.reflect.ReflectionUtils;
import scw.lang.NotFoundException;
import scw.lang.NotSupportedException;
import scw.util.value.property.PropertyFactory;

public abstract class ConstructorBeanBuilder extends AbstractBeanBuilder {
	public ConstructorBeanBuilder(BeanFactory beanFactory, PropertyFactory propertyFactory, Class<?> targetClass) {
		super(beanFactory, propertyFactory, targetClass);
	}

	protected Object createInternal(Class<?> targetClass, Constructor<? extends Object> constructor, Object[] params)
			throws Exception {
		if (isProxy()) {
			return createProxyInstance(targetClass, constructor.getParameterTypes(), params);
		}

		return constructor.newInstance(params);
	}

	protected abstract ConstructorBuilder getConstructorBuilder();

	public boolean isInstance() {
		return getConstructorBuilder().getConstructor() != null;
	}

	public Object create() throws Exception {
		if (!isInstance()) {
			throw new NotSupportedException(getTargetClass().getName());
		}

		return createInternal(getTargetClass(), getConstructorBuilder().getConstructor(),
				getConstructorBuilder().getArgs());
	}

	public Object create(Object... params) throws Exception {
		Constructor<?> constructor = ReflectionUtils.findConstructorByParameters(getTargetClass(), false, params);
		if (constructor == null) {
			throw new NotFoundException(getTargetClass() + "找不到指定的构造方法");
		}

		return createInternal(getTargetClass(), constructor, params);
	}

	public Object create(Class<?>[] parameterTypes, Object... params) throws Exception {
		Constructor<?> constructor = ReflectionUtils.getConstructor(getTargetClass(), false, parameterTypes);
		if (constructor == null) {
			throw new NotFoundException(getTargetClass() + "找不到指定的构造方法");
		}

		return createInternal(getTargetClass(), constructor, params);
	}
}
