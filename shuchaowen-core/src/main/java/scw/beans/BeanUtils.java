package scw.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import scw.aop.Filter;
import scw.aop.Proxy;
import scw.aop.ProxyUtils;
import scw.application.ApplicationConfigUtils;
import scw.beans.annotation.Autowired;
import scw.beans.annotation.Bean;
import scw.beans.annotation.Config;
import scw.beans.annotation.Configuration;
import scw.beans.annotation.Destroy;
import scw.beans.annotation.InitMethod;
import scw.beans.annotation.Service;
import scw.beans.annotation.Value;
import scw.beans.property.ValueWired;
import scw.beans.property.ValueWiredManager;
import scw.beans.xml.XmlBeanParameter;
import scw.core.Constants;
import scw.core.Init;
import scw.core.annotation.AnnotationUtils;
import scw.core.instance.InstanceFactory;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.DefaultFieldDefinition;
import scw.core.reflect.FieldDefinition;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.CompareUtils;
import scw.core.utils.ObjectUtils;
import scw.core.utils.StringUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.util.value.property.PropertyFactory;

public final class BeanUtils {
	private static Logger logger = LoggerUtils.getLogger(BeanUtils.class);

	private static Set<Class<?>> getConfigurationClassListInternal(
			Class<?> type, String packageName) {
		Set<Class<?>> list = new HashSet<Class<?>>();
		for (Class<?> clazz : ClassUtils.getClassSet(packageName)) {
			Configuration configuration = clazz
					.getAnnotation(Configuration.class);
			if (configuration == null) {
				continue;
			}

			if (!type.isAssignableFrom(clazz)) {
				continue;
			}

			if (!ClassUtils.isPresent(clazz.getName())) {
				logger.debug("not support class:{}", clazz.getName());
				continue;
			}

			list.add(clazz);
		}
		return list;
	}

	private BeanUtils() {
	};

	@SuppressWarnings("rawtypes")
	public static <T> List<Class<T>> getConfigurationClassList(
			Class<? extends T> type, Collection<Class> excludeTypes,
			PropertyFactory propertyFactory) {
		return getConfigurationClassList(type, excludeTypes, Arrays.asList(
				Constants.DEFAULT_ROOT_PACKAGE_PREFIX,
				ApplicationConfigUtils.getAnnotationPackage(propertyFactory)));
	}

	@SuppressWarnings("rawtypes")
	public static <T> List<Class<T>> getConfigurationClassList(
			Class<? extends T> type, PropertyFactory propertyFactory,
			Class... excludeTypes) {
		return getConfigurationClassList(type, Arrays.asList(excludeTypes),
				propertyFactory);
	}

	@SuppressWarnings("rawtypes")
	public static <T> List<T> getConfigurationList(Class<? extends T> type,
			Collection<Class> excludeTypes, InstanceFactory instanceFactory,
			PropertyFactory propertyFactory) {
		List<T> list = new ArrayList<T>();
		for (Class<T> clazz : getConfigurationClassList(type, excludeTypes,
				propertyFactory)) {
			if (!instanceFactory.isInstance(clazz)) {
				logger.debug("not create instance:{}", clazz);
				continue;
			}

			list.add(instanceFactory.getInstance(clazz));
		}
		return list;
	}

	@SuppressWarnings("rawtypes")
	public static <T> List<T> getConfigurationList(Class<? extends T> type,
			InstanceFactory instanceFactory, PropertyFactory propertyFactory,
			Class... excludeTypes) {
		return getConfigurationList(type, Arrays.asList(excludeTypes),
				instanceFactory, propertyFactory);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> List<Class<T>> getConfigurationClassList(
			Class<? extends T> type, Collection<Class> excludeTypes,
			Collection<String> packageNames) {
		HashSet<Class<T>> set = new HashSet<Class<T>>();
		for (String packageName : packageNames) {
			for (Class<?> clazz : getConfigurationClassListInternal(type,
					packageName)) {
				Configuration configuration = clazz
						.getAnnotation(Configuration.class);
				if (configuration == null) {
					continue;
				}

				if (!CollectionUtils.isEmpty(excludeTypes)) {
					for (Class<?> excludeType : excludeTypes) {
						if (excludeType.isAssignableFrom(clazz)) {
							continue;
						}
					}
				}
				set.add((Class<T>) clazz);
			}
		}

		List<Class<T>> list = new ArrayList<Class<T>>(set);
		for (Class<? extends T> clazz : list) {
			Configuration c = clazz.getAnnotation(Configuration.class);
			for (Class<?> e : c.excludes()) {
				if (e == clazz) {
					continue;
				}
				set.remove(e);
			}
		}

		list = new ArrayList<Class<T>>(set);
		Comparator<Class<? extends T>> comparator = new Comparator<Class<? extends T>>() {

			public int compare(Class<? extends T> o1, Class<? extends T> o2) {
				Configuration c1 = o1.getAnnotation(Configuration.class);
				Configuration c2 = o2.getAnnotation(Configuration.class);
				return CompareUtils.compare(c1.order(), c2.order(), true);
			}
		};
		Collections.sort(list, comparator);
		return list;
	}

	public static void autowired(ValueWiredManager valueWiredManager,
			BeanFactory beanFactory, PropertyFactory propertyFactory,
			Class<?> clz, Object obj, Collection<FieldDefinition> fields)
			throws Exception {
		for (FieldDefinition field : fields) {
			setBean(beanFactory, clz, obj, field);
			setConfig(beanFactory, propertyFactory, clz, obj, field);
		}

		setValue(valueWiredManager, beanFactory, propertyFactory, clz, obj,
				fields);
	}

	private static XmlBeanParameter[] sortParameters(String[] paramNames,
			Type[] parameterTypes, XmlBeanParameter[] beanMethodParameters) {
		XmlBeanParameter[] methodParameters = new XmlBeanParameter[beanMethodParameters.length];
		Type[] types = new Type[methodParameters.length];
		for (int i = 0; i < methodParameters.length; i++) {
			XmlBeanParameter beanMethodParameter = beanMethodParameters[i]
					.clone();
			if (!StringUtils.isNull(beanMethodParameter.getName())) {
				for (int a = 0; a < paramNames.length; a++) {
					if (paramNames[a].equals(beanMethodParameter.getName())) {
						types[a] = parameterTypes[a];
						methodParameters[a] = beanMethodParameters[i].clone();
						methodParameters[a].setParameterType(parameterTypes[a]);
					}
				}
			} else if (beanMethodParameter.getParameterType() != null) {
				methodParameters[i] = beanMethodParameter;
				types[i] = beanMethodParameter.getParameterType();
			} else {
				types[i] = parameterTypes[i];
				methodParameters[i] = beanMethodParameter;
				methodParameters[i].setParameterType(types[i]);
			}
		}

		return ObjectUtils.equals(Arrays.asList(parameterTypes),
				Arrays.asList(types)) ? methodParameters : null;
	}

	/**
	 * 对参数重新排序
	 * 
	 * @param executable
	 * @param beanMethodParameters
	 * @return
	 */
	public static XmlBeanParameter[] sortParameters(Method method,
			XmlBeanParameter[] beanMethodParameters) {
		if (method.getParameterTypes().length != beanMethodParameters.length) {
			return null;
		}

		return sortParameters(ParameterUtils.getParameterName(method),
				method.getParameterTypes(), beanMethodParameters);
	}

	public static XmlBeanParameter[] sortParameters(Constructor<?> constructor,
			XmlBeanParameter[] beanMethodParameters) {
		if (constructor.getParameterTypes().length != beanMethodParameters.length) {
			return null;
		}

		return sortParameters(ParameterUtils.getParameterName(constructor),
				constructor.getParameterTypes(), beanMethodParameters);
	}

	public static Object[] getBeanMethodParameterArgs(
			XmlBeanParameter[] beanParameters, InstanceFactory instanceFactory,
			PropertyFactory propertyFactory) throws Exception {
		Object[] args = new Object[beanParameters.length];
		for (int i = 0; i < args.length; i++) {
			XmlBeanParameter xmlBeanParameter = beanParameters[i];
			args[i] = xmlBeanParameter.parseValue(instanceFactory,
					propertyFactory);
		}
		return args;
	}

	private static void setConfig(BeanFactory beanFactory, PropertyFactory propertyFactory, Class<?> clz,
			Object obj, FieldDefinition field) {
		Config config = field.getAnnotation(Config.class);
		if (config != null) {
			staticFieldWarnLog(Config.class.getName(), clz, field);
			Object value = null;
			try {
				existDefaultValueWarnLog(Config.class.getName(), clz, field,
						obj);

				value = beanFactory.getInstance(config.parse()).parse(
						beanFactory, propertyFactory, field, config.value(), config.charset());
				field.set(obj, value);
			} catch (Exception e) {
				throw new RuntimeException("config：clz=" + clz.getName()
						+ ",fieldName=" + field.getField().getName(), e);
			}
		}
	}

	private static boolean checkExistDefaultValue(FieldDefinition field,
			Object obj) throws Exception {
		if (field.getField().getType().isPrimitive()) {// 值类型一定是有默认值的,所以不用判断直接所回false
			return false;
		}
		return field.get(obj) != null;
	}

	private static void existDefaultValueWarnLog(String tag, Class<?> clz,
			FieldDefinition field, Object obj) throws Exception {
		if (checkExistDefaultValue(field, obj)) {
			logger.warn("{} class[{}] fieldName[{}] existence default value",
					tag, clz.getName(), field.getField().getName());
		}
	}

	private static void staticFieldWarnLog(String tag, Class<?> clz,
			FieldDefinition field) {
		if (Modifier.isStatic(field.getField().getModifiers())) {
			logger.warn("{} class[{}] fieldName[{}] is a static field", tag,
					clz.getName(), field.getField().getName());
		}
	}

	public static Object getValueTaskId(Class<?> clazz, Object obj) {
		return obj == null ? clazz : obj;
	}

	public static void setValue(ValueWiredManager valueWiredManager,
			BeanFactory beanFactory, PropertyFactory propertyFactory,
			Class<?> clz, Object obj,
			Collection<FieldDefinition> fieldDefinitions) throws Exception {
		if (CollectionUtils.isEmpty(fieldDefinitions)) {
			return;
		}

		Collection<ValueWired> valueWireds = new LinkedList<ValueWired>();
		for (FieldDefinition field : fieldDefinitions) {
			Value value = field.getAnnotation(Value.class);
			if (value != null) {
				staticFieldWarnLog(Value.class.getName(), clz, field);
				try {
					existDefaultValueWarnLog(Value.class.getName(), clz, field,
							obj);
					ValueWired valueWired = new ValueWired(obj, field, value);
					if (valueWiredManager == null) {
						valueWired.wired(beanFactory, propertyFactory);
					} else {
						valueWireds.add(valueWired);
					}
				} catch (Throwable e) {
					throw new RuntimeException("properties：clz="
							+ clz.getName() + ",fieldName="
							+ field.getField().getName(), e);
				}
			}
		}

		if (valueWiredManager != null) {
			valueWiredManager.write(getValueTaskId(clz, obj), valueWireds);
		}
	}

	private static void setBean(BeanFactory beanFactory, Class<?> clz,
			Object obj, FieldDefinition field) {
		Autowired s = field.getAnnotation(Autowired.class);
		if (s != null) {
			staticFieldWarnLog(Autowired.class.getName(), clz, field);

			String name = s.value();
			if (name.length() == 0) {
				name = field.getField().getType().getName();
			}

			try {
				existDefaultValueWarnLog(Autowired.class.getName(), clz, field,
						obj);
				field.set(obj, beanFactory.getInstance(name));
			} catch (Exception e) {
				throw new RuntimeException("autowrite：clz=" + clz.getName()
						+ ",fieldName=" + field.getField().getName(), e);
			}
		}
	}

	public static boolean checkProxy(Class<?> type) {
		if (Modifier.isFinal(type.getModifiers())) {// final修饰的类无法代理
			return false;
		}

		if (Filter.class.isAssignableFrom(type)) {
			return false;
		}

		Bean bean = type.getAnnotation(Bean.class);
		if (bean != null) {
			return bean.proxy();
		}

		return true;
	}

	public static Proxy createProxy(BeanFactory beanFactory, Class<?> clazz,
			Collection<String> filterNames, Collection<Filter> filters) {
		return ProxyUtils.getProxyAdapter()
				.proxy(clazz,
						null,
						Arrays.asList(new RootFilter(beanFactory, filterNames,
								filters)));
	}

	public static Proxy createProxy(BeanFactory beanFactory, Class<?> clazz,
			Object service, Collection<String> filterNames,
			Collection<Filter> filters) {
		return ProxyUtils.proxyInstance(clazz, service, null, Arrays
				.asList(new RootFilter(beanFactory, filterNames, filters)));
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> getBeanList(BeanFactory beanFactory,
			Collection<String> beanNameList) {
		if (CollectionUtils.isEmpty(beanNameList)) {
			return Collections.EMPTY_LIST;
		}

		LinkedHashSet<T> set = new LinkedHashSet<T>(beanNameList.size());
		for (String name : beanNameList) {
			if (StringUtils.isEmpty(name)) {
				continue;
			}

			T t = beanFactory.getInstance(name);
			if (t == null) {
				continue;
			}

			set.add(t);
		}
		return new ArrayList<T>(set);
	}

	public static LinkedList<FieldDefinition> getAutowriteFieldDefinitionList(
			Class<?> clazz) {
		Class<?> clz = clazz;
		LinkedList<FieldDefinition> list = new LinkedList<FieldDefinition>();
		while (clz != null && clz != Object.class) {
			for (final Field field : ReflectionUtils.getDeclaredFields(clz)) {
				if (AnnotationUtils.isDeprecated(field)) {
					continue;
				}

				Autowired autowired = field.getAnnotation(Autowired.class);
				Config config = field.getAnnotation(Config.class);
				Value value = field.getAnnotation(Value.class);
				if (autowired == null && config == null && value == null) {
					continue;
				}

				field.setAccessible(true);
				if (Modifier.isStatic(field.getModifiers())) {
					logger.warn("static field not support annotation:{}",
							field.toString());
					continue;
				}

				list.add(new DefaultFieldDefinition(clz, field, false, false,
						true));
			}

			clz = clz.getSuperclass();
		}
		return list;
	}

	public static List<NoArgumentBeanMethod> getInitMethodList(Class<?> type) {
		List<NoArgumentBeanMethod> list = new ArrayList<NoArgumentBeanMethod>();
		for (Method method : AnnotationUtils.getAnnoationMethods(type, true,
				true, InitMethod.class)) {
			if (Modifier.isStatic(method.getModifiers())) {
				continue;
			}

			method.setAccessible(true);
			list.add(new NoArgumentBeanMethod(method));
		}
		return list;
	}

	public static List<NoArgumentBeanMethod> getDestroyMethdoList(Class<?> type) {
		List<NoArgumentBeanMethod> list = new ArrayList<NoArgumentBeanMethod>();
		for (Method method : AnnotationUtils.getAnnoationMethods(type, true,
				true, Destroy.class)) {
			if (Modifier.isStatic(method.getModifiers())) {
				continue;
			}

			method.setAccessible(true);
			list.add(new NoArgumentBeanMethod(method));
		}
		return list;
	}

	public static String[] getServiceNames(Class<?> clz) {
		Service service = clz.getAnnotation(Service.class);
		HashSet<String> list = new HashSet<String>();
		if (service != null) {
			for (Class<?> name : service.value()) {
				list.add(name.getName());
			}

			for (String name : service.name()) {
				list.add(name);
			}
		}

		if (list.isEmpty()) {
			Class<?>[] clzs = clz.getInterfaces();
			if (clzs != null) {
				for (Class<?> i : clzs) {
					if (AnnotationUtils.isIgnore(i)) {
						continue;
					}

					if (i.getName().startsWith("java.")
							|| i.getName().startsWith("javax.")
							|| i == scw.core.Destroy.class || i == Init.class) {
						continue;
					}

					list.add(i.getName());
				}
			}
		}
		return list.isEmpty() ? null : list.toArray(new String[list.size()]);
	}

	public static <T> void appendBean(Collection<T> beans,
			InstanceFactory instanceFactory, PropertyFactory propertyFactory,
			Class<? extends T> type, String key) {
		appendBean(beans, instanceFactory, propertyFactory, type, null, key);
	}

	@SuppressWarnings({ "unchecked" })
	public static <T> void appendBean(Collection<T> beans,
			InstanceFactory instanceFactory, PropertyFactory propertyFactory,
			Class<? extends T> type, Collection<Class<?>> excludeTypes,
			String key) {
		scw.util.value.Value value = propertyFactory.get(key);
		if(value == null){
			return ;
		}
		
		String[] filters = value.getAsObject(String[].class);
		if (ArrayUtils.isEmpty(filters)) {
			return ;
		}
		
		for (String name : filters) {
			if (!instanceFactory.isInstance(name)) {
				logger.warn("{}无法使用默认的方式实例化，请进行配置", name);
				continue;
			}

			if (!instanceFactory.isSingleton(name)) {
				logger.warn("{}不是一个单例，请进行配置", name);
				continue;
			}

			Object filter = instanceFactory.getInstance(name);
			if (!CollectionUtils.isEmpty(excludeTypes)) {
				for (Class<?> excludeType : excludeTypes) {
					if (excludeType.isInstance(filter)) {
						logger.debug("{}已被排除, excludeTypes={}", name,
								Arrays.toString(excludeTypes.toArray()));
						continue;
					}
				}
			}

			if (type.isInstance(filter)) {
				beans.add((T) filter);
			} else {
				logger.warn("{}不是一个{}类型，无法使用", name, type);
			}
		}
	}

	public static String parseRootPackage(Class<?> clazz) {
		String[] arr = StringUtils.split(clazz.getName(), '.');
		if (arr.length < 2) {
			return null;
		} else if (arr.length == 2) {
			return arr[0];
		} else {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < 2; i++) {
				if (i != 0) {
					sb.append(".");
				}
				sb.append(arr[i]);
			}

			return sb.toString();
		}
	}
}
