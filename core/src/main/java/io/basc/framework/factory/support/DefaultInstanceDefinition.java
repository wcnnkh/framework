package io.basc.framework.factory.support;

import io.basc.framework.core.parameter.ExecutableParameterDescriptorsIterator;
import io.basc.framework.core.parameter.ParameterDescriptors;
import io.basc.framework.core.parameter.ParameterFactory;
import io.basc.framework.core.parameter.ParameterUtils;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.env.Environment;
import io.basc.framework.env.EnvironmentAware;
import io.basc.framework.factory.Configurable;
import io.basc.framework.factory.InstanceDefinition;
import io.basc.framework.factory.InstanceException;
import io.basc.framework.factory.NoArgsInstanceFactory;
import io.basc.framework.factory.ServiceLoader;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.factory.annotation.Singleton;
import io.basc.framework.lang.NotFoundException;
import io.basc.framework.lang.NotSupportedException;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Iterator;

public class DefaultInstanceDefinition extends InstanceParametersFactory
		implements InstanceDefinition {
	private Class<?> targetClass;
	@SuppressWarnings("rawtypes")
	private ServiceLoader serviceLoader;
	private final ServiceLoaderFactory serviceLoaderFactory;

	public DefaultInstanceDefinition(NoArgsInstanceFactory instanceFactory,
			Environment environment, Class<?> targetClass,
			ServiceLoaderFactory serviceLoaderFactory,
			ParameterFactory defaultValueFactory) {
		super(instanceFactory, environment, defaultValueFactory);
		this.targetClass = targetClass;
		this.serviceLoaderFactory = serviceLoaderFactory;
	}

	public Class<?> getTargetClass() {
		return targetClass;
	}

	protected void configurable(Object instance) {
		if (instance instanceof EnvironmentAware) {
			((EnvironmentAware) instance).setEnvironment(getEnvironment());
		}

		if (instance instanceof Configurable) {
			((Configurable) instance).configure(serviceLoaderFactory);
		}

		if (instance instanceof DefaultValueFactoryAware) {
			((DefaultValueFactoryAware) instance)
					.setDefaultValueFactory(getDefaultValueFactory());
		}
	}

	protected Object createInternal(Class<?> targetClass,
			ParameterDescriptors parameterDescriptors, Object[] params) {
		Constructor<?> constructor = ReflectionUtils.getDeclaredConstructor(
				targetClass, parameterDescriptors.getTypes());
		Object instance = ReflectionUtils.newInstance(constructor, params);
		configurable(instance);
		return instance;
	}

	protected ParameterDescriptors getParameterDescriptors(Object[] params) {
		for (ParameterDescriptors parameterDescriptors : this) {
			if (ParameterUtils.isAssignableValue(parameterDescriptors, params)) {
				return parameterDescriptors;
			}
		}
		return null;
	}

	public boolean isInstance(Object... params) {
		return getParameterDescriptors(params) != null;
	}

	public Object create(Object... params) throws InstanceException {
		ParameterDescriptors parameterDescriptors = getParameterDescriptors(params);
		if (parameterDescriptors == null) {
			throw new NotFoundException(getTargetClass() + "找不到指定的构造方法");
		}
		return createInternal(getTargetClass(), parameterDescriptors, params);
	}

	protected ParameterDescriptors getParameterDescriptors(
			Class<?>[] parameterTypes) {
		for (ParameterDescriptors parameterDescriptors : this) {
			if (ParameterUtils.isisAssignable(parameterDescriptors,
					parameterTypes)) {
				return parameterDescriptors;
			}
		}
		return null;
	}

	public boolean isInstance(Class<?>[] parameterTypes) {
		return getParameterDescriptors(parameterTypes) != null;
	}

	public Object create(Class<?>[] parameterTypes, Object[] params)
			throws InstanceException {
		ParameterDescriptors parameterDescriptors = getParameterDescriptors(parameterTypes);
		if (parameterDescriptors == null) {
			throw new NotFoundException(getTargetClass() + "找不到指定的构造方法");
		}

		return createInternal(getTargetClass(), parameterDescriptors, params);
	}

	public Iterator<ParameterDescriptors> iterator() {
		return new ExecutableParameterDescriptorsIterator(getTargetClass());
	}

	private volatile Boolean instanced;

	public boolean isInstance() {
		if (instanced == null) {
			synchronized (this) {
				if (instanced == null) {
					instanced = isInstance(false);
				}
			}
		}
		return instanced;
	}

	protected <S> ServiceLoader<S> getServiceLoader(Class<S> clazz) {
		return serviceLoaderFactory.getServiceLoader(clazz);
	}

	private volatile ParameterDescriptors parameterDescriptors;

	public ParameterDescriptors getParameterDescriptors() {
		if (parameterDescriptors == null) {
			synchronized (this) {
				if (parameterDescriptors == null) {
					ParameterDescriptors parameterDescriptors = checkParameterDescriptors();
					if (parameterDescriptors != null) {
						this.parameterDescriptors = parameterDescriptors;
					}
				}
			}
		}
		return parameterDescriptors;
	}

	public boolean isInstance(boolean supportAbstract) {
		if (serviceLoader == null) {
			serviceLoader = getServiceLoader(getTargetClass());
		}

		if (serviceLoader.iterator().hasNext()) {
			return true;
		}

		if (!supportAbstract
				&& Modifier.isAbstract(getTargetClass().getModifiers())) {
			return false;
		}
		return getParameterDescriptors() != null;
	}

	protected ParameterDescriptors checkParameterDescriptors() {
		for (ParameterDescriptors parameterDescriptors : this) {
			if (isAccept(parameterDescriptors)) {
				return parameterDescriptors;
			}
		}
		return null;
	}

	public Object create() throws InstanceException {
		if (!isInstance()) {
			throw new NotSupportedException(getTargetClass().getName());
		}

		if (serviceLoader != null) {
			for (Object instance : serviceLoader) {
				return instance;
			}
		}

		ParameterDescriptors parameterDescriptors = getParameterDescriptors();
		return createInternal(getTargetClass(), parameterDescriptors,
				getParameters(parameterDescriptors));
	}

	@Override
	public boolean isSingleton() {
		return isSingleton(targetClass, targetClass);
	}
	
	public boolean isSingleton(Class<?> type, AnnotatedElement annotatedElement) {
		Singleton singleton = annotatedElement.getAnnotation(Singleton.class);
		if (singleton != null) {
			return singleton.value();
		}

		for (Class<?> interfaceClass : type.getInterfaces()) {
			if (!isSingleton(interfaceClass, annotatedElement)) {
				return false;
			}
		}
		// 默认是单例
		return true;
	}
}
