package scw.core.instance;

import java.util.Collection;
import java.util.Map;

import scw.core.annotation.AnnotationUtils;
import scw.core.instance.annotation.PropertyParameter;
import scw.core.instance.annotation.ResourceParameter;
import scw.core.parameter.ParameterDescriptor;
import scw.core.parameter.ParameterFactory;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.ReflectionUtils;
import scw.io.ResourceUtils;
import scw.logger.Level;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.value.Value;
import scw.value.ValueUtils;
import scw.value.property.PropertyFactory;

public class AutoSource<T> {
	private static Logger log = LoggerUtils.getLogger(AutoSource.class);
	private final Logger logger;
	private final NoArgsInstanceFactory instanceFactory;
	private final PropertyFactory propertyFactory;
	private final ParameterFactory parameterFactory;
	private final Class<?> targetClass;
	private final ParameterDescriptor[] parameterDescriptors;
	private final T source;

	public AutoSource(NoArgsInstanceFactory instanceFactory, PropertyFactory propertyFactory, Class<?> targetClass,
			ParameterDescriptor[] parameterDescriptors, T source) {
		this(instanceFactory, propertyFactory, null, targetClass, parameterDescriptors, source);
	}

	public AutoSource(NoArgsInstanceFactory instanceFactory, PropertyFactory propertyFactory,
			ParameterFactory parameterFactory, Class<?> targetClass, ParameterDescriptor[] parameterDescriptors,
			T source) {
		this(log, instanceFactory, propertyFactory, parameterFactory, targetClass, parameterDescriptors, source);
	}

	public AutoSource(Logger logger, NoArgsInstanceFactory instanceFactory, PropertyFactory propertyFactory,
			ParameterFactory parameterFactory, Class<?> targetClass, ParameterDescriptor[] parameterDescriptors,
			T source) {
		this.logger = logger;
		this.instanceFactory = instanceFactory;
		this.parameterFactory = parameterFactory;
		this.parameterDescriptors = parameterDescriptors;
		this.targetClass = targetClass;
		this.propertyFactory = propertyFactory;
		this.source = source;
	}

	public Logger getLogger() {
		return logger;
	}

	public NoArgsInstanceFactory getInstanceFactory() {
		return instanceFactory;
	}

	public PropertyFactory getPropertyFactory() {
		return propertyFactory;
	}

	public ParameterFactory getParameterFactory() {
		return parameterFactory;
	}

	public Class<?> getTargetClass() {
		return targetClass;
	}

	public ParameterDescriptor[] getParameterDescriptors() {
		return parameterDescriptors;
	}

	public final T getSource() {
		return source;
	}

	protected boolean isProerptyType(ParameterDescriptor parameterConfig) {
		PropertyParameter propertyParameter = parameterConfig.getAnnotatedElement()
				.getAnnotation(PropertyParameter.class);
		if (propertyParameter == null) {
			Class<?> type = parameterConfig.getType();
			if(ValueUtils.isCommonType(type) || type.isArray() || Collection.class.isAssignableFrom(type)
					|| Map.class.isAssignableFrom(type)){
				return true;
			}
			
			if(!ReflectionUtils.isInstance(type, true)){
				return false;
			}
			
			return type.getName().startsWith("java.") || type.getName().startsWith("javax.");
		} else {
			return propertyParameter.value();
		}
	}

	protected String getDefaultName(Class<?> clazz, ParameterDescriptor parameterDescriptor) {
		String displayName = ParameterUtils.getDisplayName(parameterDescriptor);
		if (parameterDescriptor.getName().equals(displayName)) {
			return clazz.getClass().getName() + "." + displayName;
		}
		return displayName;
	}

	protected Value getProperty(ParameterDescriptor parameterDescriptor) {
		String name = getDefaultName(targetClass, parameterDescriptor);
		Value value = propertyFactory.get(name);
		if (value == null) {
			value = ParameterUtils.getDefaultValue(parameterDescriptor);
		}

		if (value != null) {
			ResourceParameter resourceParameter = parameterDescriptor.getAnnotatedElement()
					.getAnnotation(ResourceParameter.class);
			if (resourceParameter != null) {
				if (!ResourceUtils.getResourceOperations().isExist(value.getAsString())) {
					return null;
				}
			}
		}
		return value;
	}

	protected String getInstanceName(ParameterDescriptor parameterConfig) {
		if (instanceFactory.isInstance(parameterConfig.getType())) {
			return parameterConfig.getType().getName();
		}

		String name = getDefaultName(targetClass, parameterConfig);
		if (instanceFactory.isInstance(name)) {
			return name;
		}

		return null;
	}

	protected boolean isAuto(ParameterDescriptor parameterDescriptor) {
		boolean require = !AnnotationUtils.isNullable(parameterDescriptor.getAnnotatedElement(), false);
		if (!require) {
			return true;
		}
		
		if (parameterDescriptor.getType() == targetClass) {
			return false;
		}

		if (parameterFactory != null) {
			Object value = parameterFactory.getParameter(parameterDescriptor);
			if (value != null) {
				return true;
			}
		}

		boolean isProperty = isProerptyType(parameterDescriptor);
		// 是否是属性而不是bean
		if (isProperty) {
			Value value = getProperty(parameterDescriptor);
			if (value == null) {
				return false;
			}
		} else {
			String name = getInstanceName(parameterDescriptor);
			if (name == null) {
				return false;
			}
		}
		return true;
	}

	protected boolean isAutoInternal() {
		if (parameterDescriptors == null || parameterDescriptors.length == 0) {
			return true;
		}
		
		for (int i = 0; i < parameterDescriptors.length; i++) {
			ParameterDescriptor parameterDescriptor = parameterDescriptors[i];
			try {
				boolean auto = isAuto(parameterDescriptor);
				LoggerUtils.logger(logger, auto ? Level.TRACE : Level.DEBUG, "{} parameter index {} matching: {}",
						source, i, auto ? "success" : "fail");
				if (!auto) {
					return false;
				}
			} catch (StackOverflowError e) {
				logger.error(e, "There are circular references clazz [{}] parameterName [{}] in [{}]", targetClass,
						parameterDescriptor.getName(), source);
				return false;
			}
		}
		return true;
	}

	private volatile Boolean auto = null;

	public boolean isAuto() {
		if (parameterDescriptors == null || parameterDescriptors.length == 0) {
			return true;
		}

		if (auto != null) {
			return auto;
		}

		this.auto = isAutoInternal();
		return this.auto;
	}

	protected Object getAutoValue(ParameterDescriptor parameterDescriptor) {
		boolean require = !AnnotationUtils.isNullable(parameterDescriptor.getAnnotatedElement(), false);

		if (parameterFactory != null) {
			Object value = parameterFactory.getParameter(parameterDescriptor);
			if (value != null) {
				return value;
			}
		}

		if (isProerptyType(parameterDescriptor)) {
			Value value = getProperty(parameterDescriptor);
			if (value == null) {
				if (require) {
					throw new RuntimeException(source + " require parameter:" + parameterDescriptor.getName());
				}
				return null;
			}

			return value.getAsObject(parameterDescriptor.getGenericType());
		} else {
			String name = getInstanceName(parameterDescriptor);
			return name == null ? null : instanceFactory.getInstance(name);
		}
	}

	public Object[] getAutoArgs() {
		if (parameterDescriptors == null || parameterDescriptors.length == 0) {
			return new Object[0];
		}

		Object[] args = new Object[parameterDescriptors.length];
		for (int i = 0; i < parameterDescriptors.length; i++) {
			args[i] = getAutoValue(parameterDescriptors[i]);

		}
		return args;
	}
}
