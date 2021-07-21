package scw.instance.support;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import scw.core.parameter.ExecutableParameterDescriptorsIterator;
import scw.core.parameter.ParameterDescriptors;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.ReflectionUtils;
import scw.env.Environment;
import scw.instance.InstanceDefinition;
import scw.instance.InstanceException;
import scw.instance.NoArgsInstanceFactory;
import scw.instance.ServiceLoader;
import scw.instance.ServiceLoaderFactory;
import scw.lang.NotFoundException;
import scw.lang.NotSupportedException;

public class DefaultInstanceDefinition extends InstanceParametersFactory implements InstanceDefinition {
	private Class<?> targetClass;
	@SuppressWarnings("rawtypes")
	private ServiceLoader serviceLoader;
	private final ServiceLoaderFactory serviceLoaderFactory;

	public DefaultInstanceDefinition(NoArgsInstanceFactory instanceFactory, Environment environment,
			Class<?> targetClass, ServiceLoaderFactory serviceLoaderFactory) {
		super(instanceFactory, environment);
		this.targetClass = targetClass;
		this.serviceLoaderFactory = serviceLoaderFactory;
	}

	public Class<?> getTargetClass() {
		return targetClass;
	}
	
	protected Object createInternal(Class<?> targetClass, ParameterDescriptors parameterDescriptors, Object[] params){
		Constructor<?> constructor = ReflectionUtils.findConstructor(targetClass, false, parameterDescriptors.getTypes());
		try {
			return constructor.newInstance(params == null? new Object[0]:params);
		} catch (Exception e) {
			ReflectionUtils.handleReflectionException(e);
		}
		throw new IllegalStateException("Should never get here");
	}
	
	protected ParameterDescriptors getParameterDescriptors(Object[] params){
		for (ParameterDescriptors parameterDescriptors : this) {
			if(ParameterUtils.isAssignableValue(parameterDescriptors, params)){
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
	
	protected ParameterDescriptors getParameterDescriptors(Class<?>[] parameterTypes){
		for (ParameterDescriptors parameterDescriptors : this) {
			if(ParameterUtils.isisAssignable(parameterDescriptors, parameterTypes)){
				return parameterDescriptors;
			}
		}
		return null;
	}
	
	public boolean isInstance(Class<?>[] parameterTypes) {
		return getParameterDescriptors(parameterTypes) != null;
	}

	public Object create(Class<?>[] parameterTypes, Object[] params) throws InstanceException {
		ParameterDescriptors parameterDescriptors = getParameterDescriptors(parameterTypes);
		if (parameterDescriptors == null) {
			throw new NotFoundException(getTargetClass() + "找不到指定的构造方法");
		}

		return createInternal(getTargetClass(), parameterDescriptors, params);
	}

	public Iterator<ParameterDescriptors> iterator() {
		return new ExecutableParameterDescriptorsIterator(getTargetClass());
	}

	public boolean isInstance() {
		return isInstance(false);
	}
	
	protected <S> ServiceLoader<S> getServiceLoader(Class<S> clazz){
		return serviceLoaderFactory.getServiceLoader(clazz);
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

	public Object create() throws InstanceException {
		if (!isInstance()) {
			throw new NotSupportedException(getTargetClass().getName());
		}

		if (serviceLoader != null) {
			for (Object instance : serviceLoader) {
				return instance;
			}
		}

		return createInternal(getTargetClass(), parameterDescriptors, getParameters(parameterDescriptors));
	}
}
