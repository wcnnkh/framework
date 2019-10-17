package scw.beans;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import scw.beans.annotation.Autowired;
import scw.beans.annotation.Bean;
import scw.beans.annotation.Config;
import scw.beans.annotation.Destroy;
import scw.beans.annotation.InitMethod;
import scw.beans.annotation.Service;
import scw.beans.annotation.Value;
import scw.beans.property.ValueWired;
import scw.beans.property.ValueWiredManager;
import scw.beans.xml.XmlBeanParameter;
import scw.core.Init;
import scw.core.PropertyFactory;
import scw.core.aop.Filter;
import scw.core.aop.FilterInvocationHandler;
import scw.core.aop.ProxyUtils;
import scw.core.aop.ReflectInvoker;
import scw.core.cglib.proxy.Enhancer;
import scw.core.exception.BeansException;
import scw.core.instance.InstanceFactory;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.AnnotationUtils;
import scw.core.reflect.DefaultFieldDefinition;
import scw.core.reflect.FieldDefinition;
import scw.core.reflect.ReflectUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.ObjectUtils;
import scw.core.utils.StringUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public final class BeanUtils {
	private static Logger logger = LoggerUtils.getLogger(BeanUtils.class);

	private BeanUtils() {
	};

	/**
	 * 调用init方法
	 * 
	 * @param beanFactory
	 * @param classList
	 * @throws Exception
	 */
	private static void invokerInitStaticMethod(Collection<Class<?>> classList) throws Exception {
		List<ReflectInvoker> list = new ArrayList<ReflectInvoker>();
		for (Class<?> clz : classList) {
			for (Method method : ReflectUtils.getDeclaredMethods(clz)) {
				if (!Modifier.isStatic(method.getModifiers())) {
					continue;
				}

				InitMethod initMethod = method.getAnnotation(InitMethod.class);
				if (initMethod == null) {
					continue;
				}

				if (method.getParameterTypes().length != 0) {
					throw new BeansException("ClassName=" + clz.getName() + ",MethodName=" + method.getName()
							+ "There must be no parameter.");
				}

				ReflectInvoker invoke = new ReflectInvoker(null, method);
				list.add(invoke);
			}
		}

		// 调用指定注解的方法
		CountDownLatch countDownLatch = new CountDownLatch(list.size());
		for (ReflectInvoker info : list) {
			InitProcess process = new InitProcess(info, countDownLatch);
			new Thread(process).start();
		}
		countDownLatch.await();
	}

	public synchronized static void destroyStaticMethod(ValueWiredManager valueWiredManager,
			Collection<Class<?>> classList) {
		List<ReflectInvoker> list = new ArrayList<ReflectInvoker>();
		for (Class<?> clz : classList) {
			if (valueWiredManager != null) {
				valueWiredManager.cancel(clz);
			}

			for (Method method : ReflectUtils.getDeclaredMethods(clz)) {
				if (!Modifier.isStatic(method.getModifiers())) {
					continue;
				}

				Destroy destroy = method.getAnnotation(Destroy.class);
				if (destroy == null) {
					continue;
				}

				if (method.getParameterTypes().length != 0) {
					throw new RuntimeException("ClassName=" + clz.getName() + ",MethodName=" + method.getName()
							+ "There must be no parameter.");
				}

				ReflectInvoker invoke = new ReflectInvoker(null, method);
				list.add(invoke);
			}
		}

		CountDownLatch countDownLatch = new CountDownLatch(list.size());
		for (ReflectInvoker info : list) {
			InitProcess process = new InitProcess(info, countDownLatch);
			new Thread(process).start();
		}
		
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void initStatic(ValueWiredManager valueWiredManager, BeanFactory beanFactory,
			PropertyFactory propertyFactory, Collection<Class<?>> classList) {
		try {
			initAutowriteStatic(valueWiredManager, beanFactory, propertyFactory, classList);
			invokerInitStaticMethod(classList);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void autoWrite(ValueWiredManager valueWiredManager, BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> clz, Object obj, Collection<FieldDefinition> fields)
			throws Exception {
		for (FieldDefinition field : fields) {
			setBean(beanFactory, clz, obj, field);
			setConfig(beanFactory, clz, obj, field);
		}

		setValue(valueWiredManager, beanFactory, propertyFactory, clz, obj, fields);
	}

	/**
	 * 过滤非静态方法和非静态字段
	 * 
	 * @param classList
	 */
	private static void initAutowriteStatic(ValueWiredManager valueWiredManager, BeanFactory beanFactory,
			PropertyFactory propertyFactory, Collection<Class<?>> classList) throws Exception {
		for (Class<?> clz : classList) {
			autoWrite(valueWiredManager, beanFactory, propertyFactory, clz, null,
					getAutowriteFieldDefinitionList(clz, true));
		}
	}

	private static XmlBeanParameter[] sortParameters(String[] paramNames, Type[] parameterTypes,
			XmlBeanParameter[] beanMethodParameters) {
		XmlBeanParameter[] methodParameters = new XmlBeanParameter[beanMethodParameters.length];
		Type[] types = new Type[methodParameters.length];
		for (int i = 0; i < methodParameters.length; i++) {
			XmlBeanParameter beanMethodParameter = beanMethodParameters[i].clone();
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

		return sortParameters(ParameterUtils.getParameterName(method), method.getParameterTypes(), beanMethodParameters);
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
			InstanceFactory instanceFactory, scw.core.PropertyFactory propertyFactory) throws Exception {
		Object[] args = new Object[beanParameters.length];
		for (int i = 0; i < args.length; i++) {
			XmlBeanParameter xmlBeanParameter = beanParameters[i];
			args[i] = xmlBeanParameter.parseValue(instanceFactory, propertyFactory);
		}
		return args;
	}

	private static void setConfig(BeanFactory beanFactory, Class<?> clz, Object obj, FieldDefinition field) {
		Config config = field.getAnnotation(Config.class);
		if (config != null) {
			staticFieldWarnLog(Config.class.getName(), clz, field);
			Object value = null;
			try {
				existDefaultValueWarnLog(Config.class.getName(), clz, field, obj);

				value = beanFactory.getInstance(config.parse()).parse(beanFactory, field, config.value(),
						config.charset());
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

	public static void setValue(ValueWiredManager valueWiredManager, BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> clz, Object obj, Collection<FieldDefinition> fieldDefinitions)
			throws Exception {
		if (CollectionUtils.isEmpty(fieldDefinitions)) {
			return;
		}

		Collection<ValueWired> valueWireds = new LinkedList<ValueWired>();
		for (FieldDefinition field : fieldDefinitions) {
			Value value = field.getAnnotation(Value.class);
			if (value != null) {
				staticFieldWarnLog(Value.class.getName(), clz, field);
				try {
					existDefaultValueWarnLog(Value.class.getName(), clz, field, obj);
					ValueWired valueWired = new ValueWired(obj, field, value);
					if (valueWiredManager == null) {
						valueWired.wired(beanFactory, propertyFactory);
					} else {
						valueWireds.add(valueWired);
					}
				} catch (Throwable e) {
					throw new RuntimeException(
							"properties：clz=" + clz.getName() + ",fieldName=" + field.getField().getName(), e);
				}
			}
		}

		if (valueWiredManager != null) {
			valueWiredManager.write(getValueTaskId(clz, obj), valueWireds);
		}
	}

	private static void setBean(BeanFactory beanFactory, Class<?> clz, Object obj, FieldDefinition field) {
		Autowired s = field.getAnnotation(Autowired.class);
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

	public static Enhancer createEnhancer(Class<?> clz, BeanFactory beanFactory, Filter lastFilter) {
		return createEnhancer(clz, beanFactory, null, lastFilter);
	}

	public static Enhancer createEnhancer(Class<?> clz, BeanFactory beanFactory, Collection<String> filterNames,
			Filter lastFilter) {
		Enhancer enhancer = new Enhancer();
		enhancer.setCallback(new RootFilter(beanFactory, filterNames, lastFilter));
		if (Serializable.class.isAssignableFrom(clz)) {
			enhancer.setSerialVersionUID(1L);
		}
		enhancer.setSuperclass(clz);
		return enhancer;
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

	public static <T> T proxyInterface(BeanFactory beanFactory, Class<T> interfaceClass, Filter invocation) {
		return proxyInterface(beanFactory, interfaceClass, null, invocation);
	}

	@SuppressWarnings("unchecked")
	public static <T> T proxyInterface(BeanFactory beanFactory, Class<T> interfaceClass, Collection<String> filterNames,
			Filter invocation) {
		Object newProxyInstance = java.lang.reflect.Proxy.newProxyInstance(interfaceClass.getClassLoader(),
				new Class[] { interfaceClass }, new FilterInvocationHandler(Arrays.asList(invocation)));
		return (T) ProxyUtils.proxyInstance(newProxyInstance, interfaceClass,
				new RootFilter(beanFactory, filterNames, null));
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

	public static LinkedList<FieldDefinition> getAutowriteFieldDefinitionList(Class<?> clazz, boolean isStatic) {
		Class<?> clz = clazz;
		LinkedList<FieldDefinition> list = new LinkedList<FieldDefinition>();
		while (clz != null && clz != Object.class) {
			for (final Field field : ReflectUtils.getDeclaredFields(clz)) {
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
				if (isStatic) {
					if (Modifier.isStatic(field.getModifiers())) {
						list.add(new DefaultFieldDefinition(clz, field, false, false, true, true));
					}
				} else {
					if (Modifier.isStatic(field.getModifiers())) {
						continue;
					}

					list.add(new DefaultFieldDefinition(clz, field, false, false, true, true));
				}
			}

			clz = clz.getSuperclass();
		}
		return list;
	}

	public static String getAnnotationPackage(PropertyFactory propertyFactory) {
		return propertyFactory.getProperty("scan.package");
	}

	public static String getORMPackage(PropertyFactory propertyFactory) {
		return propertyFactory.getProperty("scan.orm");
	}

	public static String getServiceAnnotationPackage(PropertyFactory propertyFactory) {
		return propertyFactory.getProperty("scan.service");
	}

	public static String getCrontabAnnotationPackage(PropertyFactory propertyFactory) {
		return propertyFactory.getProperty("scan.crontab");
	}

	public static String getConsumerAnnotationPackage(PropertyFactory propertyFactory) {
		return propertyFactory.getProperty("scan.consumer");
	}

	public static String getInitStaticPackage(PropertyFactory propertyFactory) {
		return propertyFactory.getProperty("scan.static");
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
					if(AnnotationUtils.isIgnore(i)){
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

	@SuppressWarnings({ "unchecked" })
	public static <T> void appendBean(Collection<T> beans, InstanceFactory instanceFactory,
			PropertyFactory propertyFactory, Class<T> type, String key) {
		String[] filters = StringUtils.commonSplit(propertyFactory.getProperty(key));
		if (!ArrayUtils.isEmpty(filters)) {
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
				if (type.isInstance(filter)) {
					beans.add((T) filter);
				}
			}
		}
	}
}

class InitProcess implements Runnable {
	private ReflectInvoker invoke;
	private CountDownLatch countDown;

	public InitProcess(ReflectInvoker invoke, CountDownLatch countDownLatch) {
		this.invoke = invoke;
		this.countDown = countDownLatch;
	}

	public void run() {
		try {
			invoke.invoke();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		countDown.countDown();
	}
}
