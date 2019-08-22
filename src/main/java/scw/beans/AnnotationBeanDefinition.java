package scw.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import scw.beans.annotation.Destroy;
import scw.beans.annotation.InitMethod;
import scw.beans.annotation.Service;
import scw.beans.property.ValueWiredManager;
import scw.core.Init;
import scw.core.PropertyFactory;
import scw.core.cglib.proxy.Enhancer;
import scw.core.exception.BeansException;
import scw.core.exception.NotFoundException;
import scw.core.instance.InstanceUtils;
import scw.core.reflect.FieldDefinition;
import scw.core.reflect.ReflectUtils;
import scw.core.utils.AnnotationUtils;
import scw.core.utils.ArrayUtils;

public class AnnotationBeanDefinition implements BeanDefinition {
	private final BeanFactory beanFactory;
	private final Class<?> type;
	private final String id;
	private final NoArgumentBeanMethod[] initMethods;
	private final NoArgumentBeanMethod[] destroyMethods;
	private final boolean proxy;
	private volatile Enhancer enhancer;
	private final PropertyFactory propertyFactory;
	private final String[] filterNames;
	private final boolean singleton;
	private final FieldDefinition[] autowriteFieldDefinition;
	private final String[] names;
	private final ValueWiredManager valueWiredManager;

	public AnnotationBeanDefinition(ValueWiredManager valueWiredManager, BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> type, String[] filterNames) throws Exception {
		this.valueWiredManager = valueWiredManager;
		this.beanFactory = beanFactory;
		this.type = type;
		this.propertyFactory = propertyFactory;
		this.id = type.getName();
		this.names = getServiceNames(type);
		this.initMethods = getInitMethodList(type).toArray(new NoArgumentBeanMethod[0]);
		this.destroyMethods = getDestroyMethdoList(type).toArray(new NoArgumentBeanMethod[0]);
		this.filterNames = filterNames;
		this.proxy = BeanUtils.checkProxy(type, filterNames);
		scw.beans.annotation.Bean bean = type.getAnnotation(scw.beans.annotation.Bean.class);
		this.singleton = bean == null ? true : bean.singleton();
		this.autowriteFieldDefinition = BeanUtils.getAutowriteFieldDefinitionList(type, false)
				.toArray(new FieldDefinition[0]);
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
		if (service != null && !ArrayUtils.isEmpty(service.value())) {
			return service.value();
		}

		HashSet<String> list = new HashSet<String>();
		Class<?>[] clzs = clz.getInterfaces();
		if (clzs != null) {
			for (Class<?> i : clzs) {
				if (i.getName().startsWith("java.") || i.getName().startsWith("javax.") || i == scw.core.Destroy.class
						|| i == Init.class) {
					continue;
				}

				list.add(i.getName());
			}
		}
		return list.isEmpty() ? null : list.toArray(new String[list.size()]);
	}

	public boolean isSingleton() {
		return singleton;
	}

	public boolean isProxy() {
		return this.proxy;
	}

	public String getId() {
		return this.id;
	}

	public Class<?> getType() {
		return this.type;
	}

	private Enhancer getProxyEnhancer() {
		if (enhancer == null) {
			synchronized (this) {
				if (enhancer == null) {
					enhancer = BeanUtils.createEnhancer(type, beanFactory, filterNames);
				}
			}
		}
		return enhancer;
	}

	@SuppressWarnings("unchecked")
	public <T> T create() {
		Object bean;
		try {
			if (isProxy()) {
				Enhancer enhancer = getProxyEnhancer();
				bean = enhancer.create();
			} else {
				bean = InstanceUtils.getInstance(type);
			}
			return (T) bean;
		} catch (Exception e) {
			throw new BeansException(getId(), e);
		}
	}

	public void autowrite(Object bean) throws Exception {
		BeanUtils.autoWrite(valueWiredManager, beanFactory, propertyFactory, type, bean,
				Arrays.asList(autowriteFieldDefinition));
	}

	public void init(Object bean) throws Exception {
		if (initMethods != null && initMethods.length != 0) {
			for (NoArgumentBeanMethod method : initMethods) {
				method.noArgumentInvoke(bean);
			}
		}

		if (bean instanceof Init) {
			((Init) bean).init();
		}
	}

	public void destroy(Object bean) throws Exception {
		valueWiredManager.cancel(bean);
		if (destroyMethods != null && destroyMethods.length != 0) {
			for (NoArgumentBeanMethod method : destroyMethods) {
				method.invoke(bean);
			}
		}

		if (bean instanceof scw.core.Destroy) {
			((scw.core.Destroy) bean).destroy();
		}
	}

	public String[] getNames() {
		return names;
	}

	@SuppressWarnings("unchecked")
	public <T> T create(Object... params) {
		Constructor<T> constructor = (Constructor<T>) ReflectUtils.findConstructorByParameters(getType(), true, params);
		if (constructor == null) {
			throw new NotFoundException(getId() + "找不到指定的构造方法");
		}

		Object bean;
		try {
			if (isProxy()) {
				Enhancer enhancer = getProxyEnhancer();
				bean = enhancer.create(constructor.getParameterTypes(), params);
			} else {
				bean = constructor.newInstance(params);
			}
			return (T) bean;
		} catch (Throwable e) {
			throw new BeansException(getId(), e);
		}
	}
}
