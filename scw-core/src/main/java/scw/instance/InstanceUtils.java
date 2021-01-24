package scw.instance;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import scw.core.Constants;
import scw.core.OrderComparator;
import scw.core.Ordered;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.env.SystemEnvironment;
import scw.instance.factory.DefaultInstanceFactory;
import scw.instance.factory.InstanceFactory;
import scw.instance.factory.NoArgsInstanceFactory;
import scw.instance.support.ConfigurableServiceLoader;
import scw.instance.support.SpiServiceLoader;
import scw.lang.NotSupportedException;
import scw.util.JavaVersion;
import scw.value.factory.ConvertibleValueFactory;


public final class InstanceUtils {
	private InstanceUtils() {
	};

	/**
	 * 默认的实例工厂
	 */
	public static final InstanceFactory INSTANCE_FACTORY = new DefaultInstanceFactory(
			SystemEnvironment.getInstance());

	/**
	 * 不调用构造方法实例化对象
	 */
	public static final NoArgsInstanceFactory NO_ARGS_INSTANCE_FACTORY;

	static {
		NoArgsInstanceFactory noArgsInstanceFactory = loadService(NoArgsInstanceFactory.class,
				"scw.instance.factory.SunNoArgsInstanceFactory", "scw.instance.factory.UnsafeNoArgsInstanceFactory");
		NO_ARGS_INSTANCE_FACTORY = noArgsInstanceFactory;
		if (NO_ARGS_INSTANCE_FACTORY == null) {
			throw new NotSupportedException(NoArgsInstanceFactory.class.getName());
		}
	}

	public static <T> T loadService(Class<? extends T> clazz, String... defaultNames) {
		return loadService(clazz, INSTANCE_FACTORY, SystemEnvironment.getInstance(), defaultNames);
	}

	/**
	 * 该结果是经过排序的
	 * @see Ordered
	 * @param clazz
	 * @param defaultNames
	 * @return
	 */
	public static <T> List<T> loadAllService(Class<T> clazz, String... defaultNames) {
		ServiceLoader<T> serviceLoader = getServiceLoader(clazz, INSTANCE_FACTORY, SystemEnvironment.getInstance(),
				defaultNames);
		return asList(serviceLoader);
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

	public static <T> T loadService(Class<T> clazz, NoArgsInstanceFactory instanceFactory,
			ConvertibleValueFactory<String> propertyFactory, String... defaultNames) {
		ServiceLoader<T> serviceLoader = getServiceLoader(clazz, instanceFactory, propertyFactory, defaultNames);
		return CollectionUtils.first(serviceLoader);
	}

	public static <S> ServiceLoader<S> getServiceLoader(Class<S> clazz, NoArgsInstanceFactory instanceFactory,
			ConvertibleValueFactory<String> propertyFactory, String... defaultNames) {
		return new ConfigurableServiceLoader<S>(clazz.getName().startsWith(Constants.SYSTEM_PACKAGE_NAME)? new SpiServiceLoader<S>((Class<S>)clazz, instanceFactory):null, clazz, instanceFactory, propertyFactory,
				defaultNames);
	}

	public static <T> List<T> loadAllService(Class<T> clazz, NoArgsInstanceFactory instanceFactory,
			ConvertibleValueFactory<String> propertyFactory, String... defaultNames) {
		ServiceLoader<T> serviceLoader = getServiceLoader(clazz, instanceFactory, propertyFactory, defaultNames);
		return Collections.list(CollectionUtils.toEnumeration(serviceLoader.iterator()));
	}

	public static boolean isSupport(Class<?> clazz) {
		return !ClassUtils.isPrimitiveOrWrapper(clazz) && JavaVersion.isSupported(clazz)
				&& ReflectionUtils.isPresent(clazz);
	}
}
