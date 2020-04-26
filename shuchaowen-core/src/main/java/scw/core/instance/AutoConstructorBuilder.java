package scw.core.instance;

import java.lang.reflect.Constructor;

import scw.core.parameter.ParameterDescriptorFactory;
import scw.core.parameter.ParameterFactory;
import scw.core.reflect.ReflectionUtils;
import scw.util.value.property.PropertyFactory;

public final class AutoConstructorBuilder implements ConstructorBuilder {
	private volatile ConstructorDescriptor constructorDescriptor;
	private final NoArgsInstanceFactory instanceFactory;
	private final PropertyFactory propertyFactory;
	private final Class<?> clazz;
	private final ParameterFactory parameterFactory;
	private AutoSource<Constructor<?>> autoSource;
	private final ParameterDescriptorFactory parameterDescriptorFactory;

	public AutoConstructorBuilder(NoArgsInstanceFactory instanceFactory, PropertyFactory propertyFactory,
			Class<?> clazz, ParameterDescriptorFactory parameterDescriptorFactory) {
		this(instanceFactory, propertyFactory, clazz, parameterDescriptorFactory, null);
	}

	public AutoConstructorBuilder(NoArgsInstanceFactory instanceFactory, PropertyFactory propertyFactory,
			Class<?> clazz, ParameterDescriptorFactory parameterDescriptorFactory, ParameterFactory parameterFactory) {
		this.parameterFactory = parameterFactory;
		this.clazz = clazz;
		this.propertyFactory = propertyFactory;
		this.instanceFactory = instanceFactory;
		this.parameterDescriptorFactory = parameterDescriptorFactory;
	}

	private ConstructorDescriptor getConstructorDescriptor() {
		if (constructorDescriptor == null) {
			synchronized (this) {
				if (constructorDescriptor == null) {
					for (Constructor<?> constructor : ReflectionUtils.getConstructorOrderList(clazz)) {
						this.autoSource = new AutoSource<Constructor<?>>(instanceFactory, propertyFactory,
								parameterFactory, clazz,
								parameterDescriptorFactory.getParameterDescriptors(constructor), constructor);
						if (autoSource.isAuto()) {
							ReflectionUtils.setAccessibleConstructor(constructor);
							this.constructorDescriptor = new ConstructorDescriptor(constructor,
									autoSource.getParameterDescriptors());
							break;
						}
					}

					if (constructorDescriptor == null) {
						this.constructorDescriptor = new ConstructorDescriptor(null, null);
					}
				}
			}
		}
		return constructorDescriptor;
	}

	public Object[] getArgs() throws Exception {
		return autoSource == null ? new Object[0] : autoSource.getAutoArgs();
	}

	public Constructor<?> getConstructor() {
		if (!ReflectionUtils.isInstance(clazz, false)) {
			return null;
		}

		return getConstructorDescriptor().getConstructor();
	}
}
