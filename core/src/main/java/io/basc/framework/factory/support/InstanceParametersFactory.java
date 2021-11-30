package io.basc.framework.factory.support;

import java.util.Collection;
import java.util.Map;

import io.basc.framework.core.parameter.AbstractParametersFactory;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptors;
import io.basc.framework.core.parameter.ParameterFactory;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.env.Environment;
import io.basc.framework.factory.NoArgsInstanceFactory;
import io.basc.framework.value.AnyValue;
import io.basc.framework.value.Value;
import io.basc.framework.value.ValueUtils;

public abstract class InstanceParametersFactory extends AbstractParametersFactory {
	private final NoArgsInstanceFactory instanceFactory;
	private final Environment environment;
	private final ParameterFactory defaultValueFactory;

	public InstanceParametersFactory(NoArgsInstanceFactory instanceFactory, Environment environment, ParameterFactory defaultValueFactory) {
		this.instanceFactory = instanceFactory;
		this.environment = environment;
		this.defaultValueFactory = defaultValueFactory;
	}

	public NoArgsInstanceFactory getInstanceFactory() {
		return instanceFactory;
	}

	public Environment getEnvironment() {
		return environment;
	}

	public ParameterFactory getDefaultValueFactory() {
		return defaultValueFactory;
	}

	protected boolean isProerptyType(ParameterDescriptor parameterConfig) {
		Class<?> type = parameterConfig.getType();
		if (ValueUtils.isBaseType(type) || type.isArray() || Collection.class.isAssignableFrom(type)
				|| Map.class.isAssignableFrom(type)) {
			return true;
		}

		if (!ReflectionUtils.isInstance(type)) {
			return false;
		}

		return type.getName().startsWith("java.") || type.getName().startsWith("javax.");
	}

	protected String getParameterName(ParameterDescriptors parameterDescriptors,
			ParameterDescriptor parameterDescriptor) {
		return parameterDescriptors.getDeclaringClass().getName() + "." + parameterDescriptor.getName();
	}

	protected Value getProperty(ParameterDescriptors parameterDescriptors, ParameterDescriptor parameterDescriptor) {
		String name = getParameterName(parameterDescriptors, parameterDescriptor);
		Value value = getEnvironment().getValue(name);
		if (value == null) {
			value = new AnyValue(defaultValueFactory.getParameter(parameterDescriptor));
			if(value.isEmpty()) {
				return null;
			}
		}
		return value;
	}

	protected String getInstanceName(ParameterDescriptors parameterDescriptors,
			ParameterDescriptor parameterDescriptor) {
		if (getInstanceFactory().isInstance(parameterDescriptor.getType())) {
			return parameterDescriptor.getType().getName();
		}

		String name = getParameterName(parameterDescriptors, parameterDescriptor);
		if (getInstanceFactory().isInstance(name)) {
			return name;
		}

		return null;
	}

	@Override
	protected boolean isAccept(ParameterDescriptors parameterDescriptors, ParameterDescriptor parameterDescriptor,
			int index) {
		if (parameterDescriptor.isNullable()) {
			return true;
		}

		if (parameterDescriptor.getType() == parameterDescriptors.getDeclaringClass()) {
			return false;
		}

		boolean isProperty = isProerptyType(parameterDescriptor);
		// 是否是属性而不是bean
		if (isProperty) {
			Value value = getProperty(parameterDescriptors, parameterDescriptor);
			if (value == null) {
				return false;
			}
		} else {
			String name = getInstanceName(parameterDescriptors, parameterDescriptor);
			if (name == null) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected Object getParameter(ParameterDescriptors parameterDescriptors, ParameterDescriptor parameterDescriptor,
			int index) {
		if (isProerptyType(parameterDescriptor)) {
			Value value = getProperty(parameterDescriptors, parameterDescriptor);
			if (value == null) {
				if (!parameterDescriptor.isNullable()) {
					throw new RuntimeException(
							parameterDescriptors.getSource() + " require parameter:" + parameterDescriptor.getName());
				}
				return null;
			}

			return value.getAsObject(parameterDescriptor.getGenericType());
		} else {
			String name = getInstanceName(parameterDescriptors, parameterDescriptor);
			return name == null ? null : getInstanceFactory().getInstance(name);
		}
	}
}
