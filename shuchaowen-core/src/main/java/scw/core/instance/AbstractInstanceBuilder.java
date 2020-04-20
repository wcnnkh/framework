package scw.core.instance;

import java.lang.reflect.Constructor;

import scw.core.reflect.ReflectionUtils;
import scw.lang.NotFoundException;

public abstract class AbstractInstanceBuilder<T> implements InstanceBuilder<T> {
	private Class<? extends T> targetClass;

	public AbstractInstanceBuilder(Class<? extends T> targetClass) {
		this.targetClass = targetClass;
	}

	public Class<? extends T> getTargetClass() {
		return targetClass;
	}

	public T create(Object... params) throws Exception {
		Constructor<? extends T> constructor = ReflectionUtils
				.findConstructorByParameters(getTargetClass(), false, params);
		if (constructor == null) {
			throw new NotFoundException(getTargetClass() + "找不到指定的构造方法");
		}

		return createInternal(getTargetClass(), constructor, params);
	}

	protected T createInternal(Class<?> targetClass,
			Constructor<? extends T> constructor, Object[] params)
			throws Exception {
		return constructor.newInstance(params);
	}

	public T create(Class<?>[] parameterTypes, Object... params)
			throws Exception {
		Constructor<? extends T> constructor = ReflectionUtils.getConstructor(
				getTargetClass(), false, parameterTypes);
		if (constructor == null) {
			throw new NotFoundException(getTargetClass() + "找不到指定的构造方法");
		}

		return createInternal(getTargetClass(), constructor, params);
	}
}
