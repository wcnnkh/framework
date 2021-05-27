package scw.instance.support;

import java.util.Collection;
import java.util.Map;

import scw.core.parameter.AbstractParametersFactory;
import scw.core.parameter.ParameterDescriptor;
import scw.core.parameter.ParameterDescriptors;
import scw.core.reflect.ReflectionUtils;
import scw.env.Environment;
import scw.instance.InstanceUtils;
import scw.instance.NoArgsInstanceFactory;
import scw.instance.annotation.PropertyParameter;
import scw.instance.annotation.ResourceParameter;
import scw.io.Resource;
import scw.value.Value;

public abstract class InstanceParametersFactory extends AbstractParametersFactory {
	private final NoArgsInstanceFactory instanceFactory;
	private final Environment environment;
	
	public InstanceParametersFactory(NoArgsInstanceFactory instanceFactory, Environment environment){
		this.instanceFactory = instanceFactory;
		this.environment = environment;
	}
	
	public NoArgsInstanceFactory getInstanceFactory() {
		return instanceFactory;
	}

	public Environment getEnvironment() {
		return environment;
	}

	protected boolean isProerptyType(ParameterDescriptor parameterConfig) {
		PropertyParameter propertyParameter = parameterConfig.getAnnotation(PropertyParameter.class);
		if (propertyParameter == null) {
			Class<?> type = parameterConfig.getType();
			if (Value.isBaseType(type) || type.isArray() || Collection.class.isAssignableFrom(type)
					|| Map.class.isAssignableFrom(type)) {
				return true;
			}

			if (!ReflectionUtils.isInstance(type)) {
				return false;
			}

			return type.getName().startsWith("java.") || type.getName().startsWith("javax.");
		} else {
			return propertyParameter.value();
		}
	}

	protected String getParameterName(ParameterDescriptors parameterDescriptors,
			ParameterDescriptor parameterDescriptor) {
		String displayName = InstanceUtils.getPropertyName(parameterDescriptor);
		if (parameterDescriptor.getName().equals(displayName)) {
			return parameterDescriptors.getDeclaringClass().getName() + "." + displayName;
		}
		return displayName;
	}

	protected Value getProperty(ParameterDescriptors parameterDescriptors, ParameterDescriptor parameterDescriptor) {
		String name = getParameterName(parameterDescriptors, parameterDescriptor);
		Value value = getEnvironment().getValue(name);
		if (value == null) {
			value = parameterDescriptor.getDefaultValue();
		}

		if (value != null) {
			ResourceParameter resourceParameter = parameterDescriptor.getAnnotation(ResourceParameter.class);
			if (resourceParameter != null) {
				Resource resource = environment.getResource(value.getAsString());
				if(resource == null || !resource.exists()){
					return null;
				}
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
