package scw.instance;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import scw.core.Constants;
import scw.core.OrderComparator;
import scw.core.annotation.AnnotationUtils;
import scw.core.parameter.ParameterDescriptor;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.env.SystemEnvironment;
import scw.instance.annotation.PropertyName;
import scw.instance.support.ConfigServiceLoader;
import scw.instance.support.DefaultInstanceFactory;
import scw.instance.support.ServiceLoaders;
import scw.instance.support.SpiServiceLoader;
import scw.instance.support.StaticServiceLoader;
import scw.lang.NotSupportedException;
import scw.util.Accept;
import scw.util.JavaVersion;
import scw.value.factory.ValueFactory;


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
		return CollectionUtils.first(getServiceLoader(serviceClass, defaultNames));
	}
	
	public static <S> List<S> loadAllService(Class<S> serviceClass, String... defaultNames) {
		return asList(getServiceLoader(serviceClass, defaultNames));
	}
	
	@SuppressWarnings("unchecked")
	public static <S> ServiceLoader<S> getServiceLoader(Class<S> serviceClass, String ...defaultNames){
		ServiceLoader<S> serviceLoader = INSTANCE_FACTORY.getServiceLoader(serviceClass);
		ServiceLoader<S> staticServiceLoader = new StaticServiceLoader<S>(INSTANCE_FACTORY, defaultNames);
		return new ServiceLoaders<S>(serviceLoader, staticServiceLoader);
	}
	
	public static <S> List<S> asList(ServiceLoader<S> serviceLoader){
		Iterator<S> iterator = serviceLoader.iterator();
		if(!iterator.hasNext()){
			return Collections.emptyList();
		}
		
		List<S> services = Collections.list(CollectionUtils.toEnumeration(iterator));
		Collections.sort(services, OrderComparator.INSTANCE);
		return services;
	}

	/**
	 * 注意：默认仅对{@link Constants#SYSTEM_PACKAGE_NAME}}包下的类使用spi
	 * @param serviceClass
	 * @param instanceFactory
	 * @param configFactory
	 * @param defaultNames
	 * @return
	 */
	public static <S> ServiceLoader<S> getServiceLoader(Class<S> serviceClass, NoArgsInstanceFactory instanceFactory, ValueFactory<String> configFactory, String... defaultNames) {
		return getServiceLoader(serviceClass, new Accept<Class<S>>() {

			public boolean accept(Class<S> e) {
				return e.getName().startsWith(Constants.SYSTEM_PACKAGE_NAME);
			}
		}, instanceFactory, configFactory, defaultNames);
	}
	
	@SuppressWarnings("unchecked")
	public static <S> ServiceLoader<S> getServiceLoader(Class<S> serviceClass, Accept<Class<S>> spiAccept, NoArgsInstanceFactory instanceFactory, ValueFactory<String> configFactory, String... defaultNames) {
		ServiceLoader<S> configServiceLoader = new ConfigServiceLoader<S>(serviceClass, configFactory, instanceFactory);
		ServiceLoader<S> spiServiceLoader = null;
		if(spiAccept.accept(serviceClass)){
			spiServiceLoader = new SpiServiceLoader<S>(serviceClass, instanceFactory);
		}
		ServiceLoader<S> staticServiceLoader = new StaticServiceLoader<S>(instanceFactory, defaultNames);
		return new ServiceLoaders<S>(configServiceLoader, spiServiceLoader, staticServiceLoader);
	}

	public static boolean isSupported(Class<?> clazz) {
		if(ClassUtils.isPrimitiveOrWrapper(clazz) || AnnotationUtils.isIgnore(clazz)){
			return false;
		}
		
		return ReflectionUtils.isSupported(clazz) && JavaVersion.isSupported(clazz);
	}
	
	public static String getPropertyName(ParameterDescriptor parameterDescriptor) {
		PropertyName parameterName = parameterDescriptor.getAnnotatedElement().getAnnotation(PropertyName.class);
		if (parameterName != null && StringUtils.isNotEmpty(parameterName.value())) {
			return parameterName.value();
		}
		return parameterDescriptor.getName();
	}
}
