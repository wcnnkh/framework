package scw.core.instance.definition;

import java.lang.reflect.Constructor;
import java.util.Collection;

import scw.core.instance.InstanceFactory;
import scw.core.instance.InstanceUtils;
import scw.core.parameter.ParameterDescriptor;
import scw.core.parameter.ParameterDescriptorFactory;
import scw.core.reflect.ReflectionUtils;
import scw.util.value.property.PropertyFactory;

public class AutoConstructorDefinition implements ConstructorDefinition {
	protected final InstanceFactory instanceFactory;
	protected final PropertyFactory propertyFactory;
	protected final Class<?> clazz;
	private Constructor<?> constructor;
	private ParameterDescriptor[] parameterDescriptors;

	public AutoConstructorDefinition(InstanceFactory instanceFactory,
			PropertyFactory propertyFactory, Class<?> clazz,
			ParameterDescriptorFactory parameterDescriptorFactory) {
		this(instanceFactory, propertyFactory, clazz, ReflectionUtils
				.getConstructorOrderList(clazz), parameterDescriptorFactory);
	}

	public AutoConstructorDefinition(InstanceFactory instanceFactory,
			PropertyFactory propertyFactory, Class<?> clazz,
			Collection<Constructor<?>> constructors,
			ParameterDescriptorFactory parameterDescriptorFactory) {
		this.propertyFactory = propertyFactory;
		this.instanceFactory = instanceFactory;
		this.clazz = clazz;
		for (Constructor<?> constructor : constructors) {
			ParameterDescriptor[] parameterDescriptors = parameterDescriptorFactory
					.getParameterDescriptors(constructor);
			if (InstanceUtils.isAuto(instanceFactory, propertyFactory, clazz,
					parameterDescriptors, constructor)) {
				this.constructor = constructor;
				this.parameterDescriptors = parameterDescriptors;
				break;
			}
		}
	}

	public final Constructor<?> getConstructor() {
		return constructor;
	}

	public final Object[] getArgs() {
		if (constructor == null) {
			return null;
		}

		return InstanceUtils.getAutoArgs(instanceFactory, propertyFactory,
				clazz, parameterDescriptors);
	}
}
