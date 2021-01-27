package scw.instance.support;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import scw.core.parameter.ConstructorParameterDescriptorsIterator;
import scw.core.parameter.ParameterDescriptors;
import scw.core.reflect.ReflectionUtils;
import scw.env.Environment;
import scw.instance.InstanceDefinition;
import scw.instance.InstanceException;
import scw.instance.InstanceUtils;
import scw.instance.NoArgsInstanceFactory;
import scw.instance.ServiceLoader;
import scw.lang.NotFoundException;
import scw.lang.NotSupportedException;

public class DefaultInstanceDefinition extends InstanceParameterFactory implements InstanceDefinition {
	private Class<?> targetClass;
	@SuppressWarnings("rawtypes")
	private ServiceLoader serviceLoader;

	public DefaultInstanceDefinition(NoArgsInstanceFactory instanceFactory, Environment environment,
			Class<?> targetClass) {
		super(instanceFactory, environment);
		this.targetClass = targetClass;
	}

	public Class<?> getTargetClass() {
		return targetClass;
	}

	protected Object createInternal(Class<?> targetClass, Constructor<?> constructor, Object[] params)
			throws InstanceException {
		try {
			return constructor.newInstance(params);
		} catch (Exception e) {
			ReflectionUtils.handleReflectionException(e);
		}
		throw new IllegalStateException("Should never get here");
	}
	
	public boolean isInstance(Object... params) {
		return ReflectionUtils.findConstructorByParameters(getTargetClass(), false,
				params) != null;
	}

	public Object create(Object... params) throws InstanceException {
		Constructor<?> constructor = ReflectionUtils.findConstructorByParameters(getTargetClass(), false,
				params);
		if (constructor == null) {
			throw new NotFoundException(getTargetClass() + "找不到指定的构造方法");
		}

		return createInternal(getTargetClass(), constructor, params);
	}
	
	public boolean isInstance(Class<?>[] parameterTypes) {
		return ReflectionUtils.findConstructor(getTargetClass(), false, parameterTypes) != null;
	}

	public Object create(Class<?>[] parameterTypes, Object... params) throws InstanceException {
		Constructor<?> constructor = ReflectionUtils.findConstructor(getTargetClass(), false, parameterTypes);
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
			if(targetClass.getName().contains("com.simingtang.product.service.ProductSkuStockService")){
				System.out.println(targetClass);
			}
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

		return create(parameterDescriptors.getTypes(), getParameters(parameterDescriptors));
	}
}
