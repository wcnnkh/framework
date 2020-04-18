package scw.core.instance.definition;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;

import scw.core.reflect.ReflectionUtils;
import scw.lang.NotFoundException;

public abstract class AbstractInstanceDefinition implements InstanceDefinition {
	protected final Class<?> targetClass;

	public AbstractInstanceDefinition(Class<?> targetClass) {
		this.targetClass = targetClass;
	}
	
	public String getId() {
		return getTargetClass().getName();
	}

	public Class<?> getTargetClass() {
		return targetClass;
	}

	@SuppressWarnings("unchecked")
	public <T> T create(Object... params) throws Exception {
		Constructor<?> constructor = ReflectionUtils
				.findConstructorByParameters(getTargetClass(), false, params);
		if (constructor == null) {
			throw new NotFoundException(getTargetClass() + "找不到指定的构造方法");
		}

		return (T) createInternal(getTargetClass(), constructor, params);
	}

	protected Object createInternal(Class<?> targetClass,
			Constructor<?> constructor, Object[] params) throws Exception {
		return constructor.newInstance(params);
	}

	@SuppressWarnings("unchecked")
	public <T> T create(Class<?>[] parameterTypes, Object... params)
			throws Exception {
		Constructor<?> constructor = ReflectionUtils.getConstructor(
				getTargetClass(), false, parameterTypes);
		if (constructor == null) {
			throw new NotFoundException(getTargetClass() + "找不到指定的构造方法");
		}

		return (T) createInternal(getTargetClass(), constructor, params);
	}

	public boolean isSingleton() {
		return false;
	}
	
	public AnnotatedElement getAnnotatedElement() {
		return getTargetClass();
	}
}
