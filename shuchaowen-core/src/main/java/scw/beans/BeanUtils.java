package scw.beans;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import scw.aop.Filter;
import scw.aop.Proxy;
import scw.beans.annotation.Autowired;
import scw.beans.annotation.Bean;
import scw.beans.annotation.Config;
import scw.beans.annotation.Destroy;
import scw.beans.annotation.InitMethod;
import scw.beans.annotation.Service;
import scw.beans.annotation.Value;
import scw.beans.xml.XmlBeanParameter;
import scw.core.GlobalPropertyFactory;
import scw.core.Init;
import scw.core.annotation.AnnotationUtils;
import scw.core.instance.InstanceFactory;
import scw.core.instance.InstanceUtils;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.DefaultFieldDefinition;
import scw.core.reflect.FieldDefinition;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.ObjectUtils;
import scw.core.utils.StringUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.util.value.property.PropertyFactory;

public final class BeanUtils {
	private static Logger logger = LoggerUtils.getLogger(BeanUtils.class);

	private BeanUtils() {
	};

	public static void autowired(BeanFactory beanFactory, PropertyFactory propertyFactory, Class<?> clz, Object obj,
			Collection<FieldDefinition> fields) throws Exception {
		for (FieldDefinition field : fields) {
			setBean(beanFactory, clz, obj, field);
			setConfig(beanFactory, propertyFactory, clz, obj, field);
			setValue(beanFactory, propertyFactory, clz, obj, field);
		}
	}

	private static XmlBeanParameter[] sortParameters(String[] paramNames, Class<?>[] parameterTypes,
			XmlBeanParameter[] beanMethodParameters) {
		XmlBeanParameter[] methodParameters = new XmlBeanParameter[beanMethodParameters.length];
		Class<?>[] types = new Class<?>[methodParameters.length];
		for (int i = 0; i < methodParameters.length; i++) {
			XmlBeanParameter beanMethodParameter = beanMethodParameters[i].clone();
			if (!StringUtils.isNull(beanMethodParameter.getName())) {
				for (int a = 0; a < paramNames.length; a++) {
					if (paramNames[a].equals(beanMethodParameter.getName())) {
						types[a] = parameterTypes[a];
						methodParameters[a] = beanMethodParameters[i].clone();
						methodParameters[a].setType(parameterTypes[a]);
					}
				}
			} else if (beanMethodParameter.getType() != null) {
				methodParameters[i] = beanMethodParameter;
				types[i] = beanMethodParameter.getType();
			} else {
				types[i] = parameterTypes[i];
				methodParameters[i] = beanMethodParameter;
				methodParameters[i].setType(types[i]);
			}
		}

		return ObjectUtils.equals(Arrays.asList(parameterTypes), Arrays.asList(types)) ? methodParameters : null;
	}

	/**
	 * 对参数重新排序
	 * 
	 * @param executable
	 * @param beanMethodParameters
	 * @return
	 */
	public static XmlBeanParameter[] sortParameters(Method method, XmlBeanParameter[] beanMethodParameters) {
		if (method.getParameterTypes().length != beanMethodParameters.length) {
			return null;
		}

		return sortParameters(ParameterUtils.getParameterName(method), method.getParameterTypes(),
				beanMethodParameters);
	}

	public static XmlBeanParameter[] sortParameters(Constructor<?> constructor,
			XmlBeanParameter[] beanMethodParameters) {
		if (constructor.getParameterTypes().length != beanMethodParameters.length) {
			return null;
		}

		return sortParameters(ParameterUtils.getParameterName(constructor), constructor.getParameterTypes(),
				beanMethodParameters);
	}

	public static Object[] getBeanMethodParameterArgs(XmlBeanParameter[] beanParameters,
			InstanceFactory instanceFactory, PropertyFactory propertyFactory) throws Exception {
		Object[] args = new Object[beanParameters.length];
		for (int i = 0; i < args.length; i++) {
			XmlBeanParameter xmlBeanParameter = beanParameters[i];
			args[i] = xmlBeanParameter.parseValue(instanceFactory, propertyFactory);
		}
		return args;
	}

	private static void setConfig(BeanFactory beanFactory, PropertyFactory propertyFactory, Class<?> clz, Object obj,
			FieldDefinition field) {
		Config config = field.getAnnotatedElement().getAnnotation(Config.class);
		if (config != null) {
			staticFieldWarnLog(Config.class.getName(), clz, field);
			Object value = null;
			try {
				existDefaultValueWarnLog(Config.class.getName(), clz, field, obj);

				value = beanFactory.getInstance(config.parse()).parse(beanFactory, propertyFactory, field,
						config.value(), config.charset());
				field.set(obj, value);
			} catch (Exception e) {
				throw new RuntimeException("config：clz=" + clz.getName() + ",fieldName=" + field.getField().getName(),
						e);
			}
		}
	}

	private static boolean checkExistDefaultValue(FieldDefinition field, Object obj) throws Exception {
		if (field.getField().getType().isPrimitive()) {// 值类型一定是有默认值的,所以不用判断直接所回false
			return false;
		}
		return field.get(obj) != null;
	}

	private static void existDefaultValueWarnLog(String tag, Class<?> clz, FieldDefinition field, Object obj)
			throws Exception {
		if (checkExistDefaultValue(field, obj)) {
			logger.warn("{} class[{}] fieldName[{}] existence default value", tag, clz.getName(),
					field.getField().getName());
		}
	}

	private static void staticFieldWarnLog(String tag, Class<?> clz, FieldDefinition field) {
		if (Modifier.isStatic(field.getField().getModifiers())) {
			logger.warn("{} class[{}] fieldName[{}] is a static field", tag, clz.getName(), field.getField().getName());
		}
	}

	public static Object getValueTaskId(Class<?> clazz, Object obj) {
		return obj == null ? clazz : obj;
	}

	public static void setValue(BeanFactory beanFactory, PropertyFactory propertyFactory, Class<?> clz, Object obj,
			FieldDefinition field) throws Exception {
		Value value = field.getAnnotatedElement().getAnnotation(Value.class);
		if (value != null) {
			staticFieldWarnLog(Value.class.getName(), clz, field);
			try {
				existDefaultValueWarnLog(Value.class.getName(), clz, field, obj);
				Object v = beanFactory.getInstance(value.format()).format(beanFactory, propertyFactory, field,
						value.value());
				if (v != null) {
					field.set(obj, v);
				}
			} catch (Throwable e) {
				throw new RuntimeException("value：clz=" + clz.getName() + ",fieldName=" + field.getField().getName(),
						e);
			}
		}
	}

	private static void setBean(BeanFactory beanFactory, Class<?> clz, Object obj, FieldDefinition field) {
		Autowired s = field.getAnnotatedElement().getAnnotation(Autowired.class);
		if (s != null) {
			staticFieldWarnLog(Autowired.class.getName(), clz, field);

			String name = s.value();
			if (name.length() == 0) {
				name = field.getField().getType().getName();
			}

			try {
				existDefaultValueWarnLog(Autowired.class.getName(), clz, field, obj);
				field.set(obj, beanFactory.getInstance(name));
			} catch (Exception e) {
				throw new RuntimeException(
						"autowrite：clz=" + clz.getName() + ",fieldName=" + field.getField().getName(), e);
			}
		}
	}

	public static boolean isSingletion(Class<?> type, AnnotatedElement annotatedElement) {
		Bean bean = annotatedElement.getAnnotation(Bean.class);
		return bean == null ? true : bean.singleton();
	}

	public static boolean isProxy(Class<?> type, AnnotatedElement annotatedElement) {
		if (Modifier.isFinal(type.getModifiers())) {// final修饰的类无法代理
			return false;
		}

		if (Filter.class.isAssignableFrom(type)) {
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> getBeanList(BeanFactory beanFactory, Collection<String> beanNameList) {
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

	public static LinkedList<FieldDefinition> getAutowriteFieldDefinitionList(Class<?> clazz) {
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
					logger.warn("static field not support annotation:{}", field.toString());
					continue;
				}

				list.add(new DefaultFieldDefinition(clz, field, false, false, true));
			}

			clz = clz.getSuperclass();
		}
		return list;
	}

	public static List<NoArgumentBeanMethod> getInitMethodList(Class<?> type) {
		List<NoArgumentBeanMethod> list = new ArrayList<NoArgumentBeanMethod>();
		for (Method method : AnnotationUtils.getAnnoationMethods(type, true, true, InitMethod.class)) {
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
		for (Method method : AnnotationUtils.getAnnoationMethods(type, true, true, Destroy.class)) {
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

					if (i.getName().startsWith("java.") || i.getName().startsWith("javax.")
							|| i == scw.core.Destroy.class || i == Init.class) {
						continue;
					}

					list.add(i.getName());
				}
			}
		}
		return list.isEmpty() ? null : list.toArray(new String[list.size()]);
	}

	public static String getScanAnnotationPackageName() {
		return GlobalPropertyFactory.getInstance().getValue("scw.scan.beans.package", String.class,
				InstanceUtils.getScanAnnotationPackageName());
	}
}
