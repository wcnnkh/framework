package scw.core.instance;

import java.lang.reflect.Constructor;

import scw.core.parameter.ParameterFactory;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.ReflectionUtils;
import scw.value.property.PropertyFactory;

public final class AutoConstructorBuilder implements ConstructorBuilder {
	private volatile ConstructorDescriptor constructorDescriptor;
	private final NoArgsInstanceFactory instanceFactory;
	private final PropertyFactory propertyFactory;
	private final Class<?> clazz;
	private final ParameterFactory parameterFactory;
	private AutoSource<Constructor<?>> autoSource;

	public AutoConstructorBuilder(NoArgsInstanceFactory instanceFactory, PropertyFactory propertyFactory,
			Class<?> clazz) {
		this(instanceFactory, propertyFactory, clazz, null);
	}

	public AutoConstructorBuilder(NoArgsInstanceFactory instanceFactory, PropertyFactory propertyFactory,
			Class<?> clazz, ParameterFactory parameterFactory) {
		this.parameterFactory = parameterFactory;
		this.clazz = clazz;
		this.propertyFactory = propertyFactory;
		this.instanceFactory = instanceFactory;
	}

	private ConstructorDescriptor getConstructorDescriptor() {
		if (constructorDescriptor == null) {
			synchronized (this) {
				if (constructorDescriptor == null) {
					for (Constructor<?> constructor : ReflectionUtils.getConstructorOrderList(clazz)) {
						this.autoSource = new AutoSource<Constructor<?>>(instanceFactory, propertyFactory,
								parameterFactory, clazz, ParameterUtils.getParameterDescriptors(constructor),
								constructor);
						if (autoSource.isAuto()) {
							ReflectionUtils.makeAccessible(constructor);
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
