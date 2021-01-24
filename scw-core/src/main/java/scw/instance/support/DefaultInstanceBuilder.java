package scw.instance.support;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import scw.core.parameter.ConstructorParameterDescriptorsIterator;
import scw.core.parameter.ParameterDescriptors;
import scw.core.reflect.ReflectionUtils;
import scw.env.Environment;
import scw.instance.InstanceBuilder;
import scw.instance.InstanceUtils;
import scw.instance.ServiceLoader;
import scw.instance.factory.InstanceParameterFactory;
import scw.instance.factory.NoArgsInstanceFactory;
import scw.lang.NotFoundException;
import scw.lang.NotSupportedException;

public class DefaultInstanceBuilder<T> extends InstanceParameterFactory implements InstanceBuilder<T> {
	private Class<T> targetClass;
	private ServiceLoader<T> serviceLoader;

	public DefaultInstanceBuilder(NoArgsInstanceFactory instanceFactory, Environment environment,
			Class<T> targetClass) {
		super(instanceFactory, environment);
		this.targetClass = targetClass;
	}

	public Class<? extends T> getTargetClass() {
		return targetClass;
	}

	protected T createInternal(Class<?> targetClass, Constructor<? extends T> constructor, Object[] params)
			throws Exception {
		try {
			return constructor.newInstance(params);
		} catch (Exception e) {
			ReflectionUtils.handleReflectionException(e);
		}
		throw new IllegalStateException("Should never get here");
	}

	public T create(Object... params) throws Exception {
		Constructor<? extends T> constructor = ReflectionUtils.findConstructorByParameters(getTargetClass(), false,
				params);
		if (constructor == null) {
			throw new NotFoundException(getTargetClass() + "找不到指定的构造方法");
		}

		return createInternal(getTargetClass(), constructor, params);
	}

	public T create(Class<?>[] parameterTypes, Object... params) throws Exception {
		Constructor<? extends T> constructor = ReflectionUtils.getConstructor(getTargetClass(), false, parameterTypes);
		if (constructor == null) {
			throw new NotFoundException(getTargetClass() + "找不到指定的构造方法");
		}

		return createInternal(getTargetClass(), constructor, params);
	}

	public Iterator<ParameterDescriptors> iterator() {
		return new ConstructorParameterDescriptorsIterator(getTargetClass());
	}

	public boolean isInstance() {
		return isInstance(false);
	}
	
	protected <S> ServiceLoader<S> getServiceLoader(Class<S> clazz){
		return InstanceUtils.getServiceLoader(clazz, getInstanceFactory(), getEnvironment());
	}

	private final AtomicBoolean init = new AtomicBoolean(false);
	private volatile ParameterDescriptors parameterDescriptors;

	public boolean isInstance(boolean supportAbstract) {
		if (init.get()) {
			if (serviceLoader != null && serviceLoader.iterator().hasNext()) {
				return true;
			}

			return parameterDescriptors != null;
		}

		if (init.compareAndSet(false, true)) {
			if (serviceLoader == null) {
				serviceLoader = getServiceLoader(targetClass);
			}
			
			if(serviceLoader.iterator().hasNext()){
				return true;
			}
			
			if (!supportAbstract && Modifier.isAbstract(getTargetClass().getModifiers())) {
				return false;
			}
			
			ParameterDescriptors parameterDescriptors = checkParameterDescriptors();
			if(parameterDescriptors != null){
				this.parameterDescriptors = parameterDescriptors;
				return true;
			}
		}
		return false;
	}
	
	protected ParameterDescriptors checkParameterDescriptors(){
		for (ParameterDescriptors parameterDescriptors : this) {
			if (isAccept(parameterDescriptors)) {
				return parameterDescriptors;
			}
		}
		return null;
	}

	public T create() throws Exception {
		if (!isInstance()) {
			throw new NotSupportedException(getTargetClass().getName());
		}

		if (serviceLoader != null) {
			for (T instance : serviceLoader) {
				return instance;
			}
		}

		return create(parameterDescriptors.getTypes(), getParameters(parameterDescriptors));
	}
}
