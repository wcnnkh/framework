package scw.core.instance;

import java.lang.reflect.Constructor;

import scw.core.parameter.ParameterDescriptor;
import scw.core.parameter.ParameterDescriptorFactory;
import scw.core.parameter.ParameterFactory;
import scw.core.reflect.ReflectionUtils;
import scw.util.value.property.PropertyFactory;

public final class AutoInstanceBuilder implements InstanceBuilder {
	private volatile ConstructorDescriptor constructorDescriptor;
	private final InstanceFactory instanceFactory;
	private final PropertyFactory propertyFactory;
	private final Class<?> clazz;
	private final ParameterFactory parameterFactory;
	private final ParameterDescriptorFactory parameterDescriptorFactory;

	public AutoInstanceBuilder(InstanceFactory instanceFactory,
			PropertyFactory propertyFactory, Class<?> clazz,
			ParameterDescriptorFactory parameterDescriptorFactory) {
		this(instanceFactory, propertyFactory, clazz,
				parameterDescriptorFactory, null);
	}

	public AutoInstanceBuilder(InstanceFactory instanceFactory,
			PropertyFactory propertyFactory, Class<?> clazz,
			ParameterDescriptorFactory parameterDescriptorFactory,
			ParameterFactory parameterFactory) {
		this.parameterFactory = parameterFactory;
		this.clazz = clazz;
		this.propertyFactory = propertyFactory;
		this.instanceFactory = instanceFactory;
		this.parameterDescriptorFactory = parameterDescriptorFactory;
	}

	public ConstructorDescriptor getConstructorDescriptor() {
		if (constructorDescriptor == null) {
			synchronized (this) {
				if (constructorDescriptor == null) {
					for (Constructor<?> constructor : ReflectionUtils
							.getConstructorOrderList(clazz)) {
						ParameterDescriptor[] parameterDescriptors = parameterDescriptorFactory
								.getParameterDescriptors(constructor);
						if (InstanceUtils.isAuto(instanceFactory,
								propertyFactory, clazz, parameterDescriptors,
								parameterFactory, constructor)) {
							ReflectionUtils
									.setAccessibleConstructor(constructor);
							this.constructorDescriptor = new ConstructorDescriptor(
									constructor, parameterDescriptors);
							break;
						}
					}

					if (constructorDescriptor == null) {
						this.constructorDescriptor = new ConstructorDescriptor(
								null, null);
					}
				}
			}
		}
		return constructorDescriptor;
	}

	public Object[] getArgs() throws Exception {
		return InstanceUtils.getAutoArgs(instanceFactory, propertyFactory,
				clazz, getConstructorDescriptor().getParameterDescriptors(),
				parameterFactory);
	}

	public Constructor<?> getConstructor() {
		return getConstructorDescriptor().getConstructor();
	}
}
