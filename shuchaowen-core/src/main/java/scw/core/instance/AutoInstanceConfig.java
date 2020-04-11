package scw.core.instance;

import java.lang.reflect.Constructor;
import java.util.Collection;

import scw.core.parameter.ParameterDescriptor;
import scw.core.parameter.ParameterDescriptorFactory;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.ReflectionUtils;
import scw.util.value.property.PropertyFactory;

public class AutoInstanceConfig implements InstanceConfig {
	protected final InstanceFactory instanceFactory;
	protected final PropertyFactory propertyFactory;
	protected final Class<?> clazz;
	private Constructor<?> constructor;
	private ParameterDescriptor[] parameterConfigs;

	public AutoInstanceConfig(InstanceFactory instanceFactory, PropertyFactory propertyFactory, Class<?> clazz) {
		this(instanceFactory, propertyFactory, clazz,
				ReflectionUtils.getConstructorOrderList(clazz), ParameterUtils.getParameterDescriptorFactory());
	}

	public AutoInstanceConfig(InstanceFactory instanceFactory, PropertyFactory propertyFactory, Class<?> clazz,
			Collection<Constructor<?>> constructors, ParameterDescriptorFactory parameterDescriptorFactory) {
		this.propertyFactory = propertyFactory;
		this.instanceFactory = instanceFactory;
		this.clazz = clazz;
		for (Constructor<?> constructor : constructors) {
			if (isAutoConstructor(constructor)) {
				this.constructor = constructor;
				this.parameterConfigs = parameterDescriptorFactory.getParameterDescriptors(constructor);
				break;
			}
		}
	}

	public final boolean isAutoConstructor(Constructor<?> constructor) {
		return InstanceUtils.isAuto(instanceFactory, propertyFactory, clazz,
				ParameterUtils.getParameterDescriptors(constructor), constructor);
	}

	public final Constructor<?> getConstructor() {
		return constructor;
	}

	public final Object[] getArgs() {
		if (constructor == null) {
			return null;
		}

		return InstanceUtils.getAutoArgs(instanceFactory, propertyFactory, clazz, parameterConfigs);
	}
}
