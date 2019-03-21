package scw.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import net.sf.cglib.proxy.Enhancer;
import scw.beans.annotaion.Destroy;
import scw.beans.annotaion.InitMethod;
import scw.beans.annotaion.Service;
import scw.beans.property.PropertiesFactory;
import scw.common.ClassInfo;
import scw.common.FieldInfo;
import scw.common.exception.BeansException;
import scw.common.utils.ClassUtils;
import scw.common.utils.StringUtils;

public class AnnotationBean implements Bean {
	private final BeanFactory beanFactory;
	private final Class<?> type;
	private final String id;
	private volatile Constructor<?> constructor;
	private final BeanMethod[] initMethods;
	private final BeanMethod[] destroyMethods;
	private final boolean proxy;
	private Enhancer enhancer;
	private final PropertiesFactory propertiesFactory;
	private String[] filterNames;
	private final boolean singleton;

	public AnnotationBean(BeanFactory beanFactory, PropertiesFactory propertiesFactory, Class<?> type,
			String[] filterNames) throws Exception {
		this.beanFactory = beanFactory;
		this.type = type;
		this.propertiesFactory = propertiesFactory;

		String id = ClassUtils.getProxyRealClassName(type);
		Service service = type.getAnnotation(Service.class);
		if (service != null) {
			Class<?>[] interfaces = type.getInterfaces();
			if (interfaces.length != 0) {
				id = interfaces[0].getName();
			}

			if (!StringUtils.isNull(service.value())) {
				id = service.value();
			}
		}
		this.id = id;

		this.initMethods = getInitMethodList(type).toArray(new BeanMethod[0]);
		this.destroyMethods = getDestroyMethdoList(type).toArray(new BeanMethod[0]);
		this.filterNames = filterNames;
		this.proxy = BeanUtils.checkProxy(type, filterNames);
		scw.beans.annotaion.Bean bean = type.getAnnotation(scw.beans.annotaion.Bean.class);
		this.singleton = bean == null ? true : bean.singleton();
	}

	public static List<BeanMethod> getInitMethodList(Class<?> type) {
		List<BeanMethod> list = new ArrayList<BeanMethod>();
		for (Method method : ClassUtils.getAnnoationMethods(type, true, true, InitMethod.class)) {
			if (Modifier.isStatic(method.getModifiers())) {
				continue;
			}

			method.setAccessible(true);
			list.add(new NoArgumentBeanMethod(method));
		}
		return list;
	}

	public static List<BeanMethod> getDestroyMethdoList(Class<?> type) {
		List<BeanMethod> list = new ArrayList<BeanMethod>();
		for (Method method : ClassUtils.getAnnoationMethods(type, true, true, Destroy.class)) {
			if (Modifier.isStatic(method.getModifiers())) {
				continue;
			}

			method.setAccessible(true);
			list.add(new NoArgumentBeanMethod(method));
		}
		return list;
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
			enhancer = BeanUtils.createEnhancer(type, beanFactory, filterNames);
		}
		return enhancer;
	}

	@SuppressWarnings("unchecked")
	public <T> T newInstance() {
		if (constructor == null) {
			synchronized (this) {
				if (constructor == null) {
					try {
						this.constructor = type.getDeclaredConstructor();// 不用考虑并发
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					}
				}
			}
		}

		Object bean;
		try {
			if (isProxy()) {
				Enhancer enhancer = getProxyEnhancer();
				bean = enhancer.create();
			} else {
				bean = constructor.newInstance();
			}
			return (T) bean;
		} catch (Exception e) {
			throw new BeansException(getId(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T newInstance(Class<?>[] parameterTypes, Object... args) {
		Object bean;
		try {
			if (isProxy()) {
				Enhancer enhancer = getProxyEnhancer();
				bean = enhancer.create(parameterTypes, args);
			} else {
				bean = type.getDeclaredConstructor(parameterTypes).newInstance(args);
			}
			return (T) bean;
		} catch (Exception e) {
			throw new BeansException(getId(), e);
		}
	}

	public void autowrite(Object bean) throws Exception {
		ClassInfo classInfo = ClassUtils.getClassInfo(type);
		while (classInfo != null) {
			for (FieldInfo field : classInfo.getFieldMap().values()) {
				if (Modifier.isStatic(field.getField().getModifiers())) {
					continue;
				}

				BeanUtils.autoWrite(type, beanFactory, propertiesFactory, bean, field);
			}
			classInfo = classInfo.getSuperInfo();
		}
	}

	public void init(Object bean) throws Exception {
		if (initMethods != null && initMethods.length != 0) {
			for (BeanMethod method : initMethods) {
				method.invoke(bean, beanFactory, propertiesFactory);
			}
		}
	}

	public void destroy(Object bean) throws Exception {
		if (destroyMethods != null && destroyMethods.length != 0) {
			for (BeanMethod method : destroyMethods) {
				method.invoke(bean, beanFactory, propertiesFactory);
			}
		}
	}

	public String[] getNames() {
		return null;
	}
}
