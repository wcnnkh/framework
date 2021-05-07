package scw.instance;

import java.util.List;

import scw.core.annotation.AnnotationUtils;
import scw.core.parameter.ParameterDescriptor;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.env.SystemEnvironment;
import scw.instance.annotation.PropertyName;
import scw.instance.support.DefaultInstanceFactory;
import scw.instance.support.DefaultServiceLoaderFactory;
import scw.lang.NotSupportedException;
import scw.util.JavaVersion;


public final class InstanceUtils {
	private InstanceUtils() {
	};

	/**
	 * 默认的实例工厂
	 */
	public static final DefaultInstanceFactory INSTANCE_FACTORY = new DefaultInstanceFactory(
			SystemEnvironment.getInstance(), true);
	
	/**
	 * 不调用构造方法实例化对象
	 */
	public static final NoArgsInstanceFactory NO_ARGS_INSTANCE_FACTORY;

	static {
		NoArgsInstanceFactory noArgsInstanceFactory = loadService(NoArgsInstanceFactory.class,
				"scw.instance.support.SunNoArgsInstanceFactory", "scw.instance.support.UnsafeNoArgsInstanceFactory");
		NO_ARGS_INSTANCE_FACTORY = noArgsInstanceFactory;
		if (NO_ARGS_INSTANCE_FACTORY == null) {
			throw new NotSupportedException(NoArgsInstanceFactory.class.getName());
		}
	}
	
	public static <S> S loadService(Class<S> serviceClass, String ...defaultNames){
		return getServiceLoader(serviceClass, defaultNames).getFirst();
	}
	
	public static <S> List<S> loadAllService(Class<S> serviceClass, String... defaultNames) {
		return getServiceLoader(serviceClass, defaultNames).toList();
	}
	
	public static <S> ServiceLoader<S> getServiceLoader(Class<S> serviceClass, String ...defaultNames){
		DefaultServiceLoaderFactory serviceLoaderFactory = new DefaultServiceLoaderFactory(INSTANCE_FACTORY, SystemEnvironment.getInstance());
		return serviceLoaderFactory.getServiceLoader(serviceClass, defaultNames);
	}
	
	public static boolean isSupported(Class<?> clazz) {
		if(ClassUtils.isPrimitiveOrWrapper(clazz) || AnnotationUtils.isIgnore(clazz)){
			return false;
		}
		
		return ReflectionUtils.isSupported(clazz) && JavaVersion.isSupported(clazz);
	}
	
	public static String getPropertyName(ParameterDescriptor parameterDescriptor) {
		PropertyName parameterName = parameterDescriptor.getAnnotation(PropertyName.class);
		if (parameterName != null && StringUtils.isNotEmpty(parameterName.value())) {
			return parameterName.value();
		}
		return parameterDescriptor.getName();
	}
}
