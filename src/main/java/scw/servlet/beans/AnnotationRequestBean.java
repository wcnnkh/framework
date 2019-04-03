package scw.servlet.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import net.sf.cglib.proxy.Enhancer;
import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.beans.annotation.Destroy;
import scw.beans.annotation.InitMethod;
import scw.beans.property.PropertiesFactory;
import scw.common.ClassInfo;
import scw.common.FieldInfo;
import scw.common.exception.BeansException;
import scw.common.utils.ClassUtils;
import scw.servlet.Request;

public final class AnnotationRequestBean implements RequestBean {
	private final BeanFactory beanFactory;
	private final Class<?> type;
	private final String id;
	private Constructor<?> constructor;
	private final Method[] initMethods;
	private final Method[] destroyMethods;
	private final boolean proxy;
	private Enhancer enhancer;
	private final PropertiesFactory propertiesFactory;
	private ClassInfo classInfo;

	public AnnotationRequestBean(BeanFactory beanFactory, PropertiesFactory propertiesFactory, Class<?> type,
			String[] filterNames) throws Exception {
		this.beanFactory = beanFactory;
		this.type = type;
		this.propertiesFactory = propertiesFactory;
		this.id = ClassUtils.getProxyRealClassName(type);

		List<Method> initMethodList = new ArrayList<Method>();
		List<Method> destroyMethodList = new ArrayList<Method>();
		Class<?> tempClz = type;
		for (Method method : tempClz.getDeclaredMethods()) {
			if (Modifier.isStatic(method.getModifiers())) {
				continue;
			}

			InitMethod initMethod = method.getAnnotation(InitMethod.class);
			if (initMethod != null) {
				method.setAccessible(true);
				initMethodList.add(method);
			}

			Destroy destroy = method.getAnnotation(Destroy.class);
			if (destroy != null) {
				method.setAccessible(true);
				destroyMethodList.add(method);
			}
		}
		this.initMethods = initMethodList.toArray(new Method[initMethodList.size()]);
		this.destroyMethods = destroyMethodList.toArray(new Method[destroyMethodList.size()]);

		this.proxy = BeanUtils.checkProxy(type, filterNames);
		this.constructor = getAnnotationRequestBeanConstructor(type);
		if (this.constructor == null) {
			throw new BeansException("not found constructor");
		}
		enhancer = BeanUtils.createEnhancer(type, beanFactory, filterNames);
		this.classInfo = ClassUtils.getClassInfo(type);
	}

	public static Constructor<?> getAnnotationRequestBeanConstructor(Class<?> type) {
		try {
			return type.getDeclaredConstructor(Request.class);
		} catch (NoSuchMethodException e) {
		} catch (SecurityException e) {
		}
		return null;
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

	@SuppressWarnings("unchecked")
	public <T> T newInstance(Request request) {
		Object bean;
		try {
			if (isProxy()) {
				bean = constructor.getParameterCount() == 0 ? enhancer.create()
						: enhancer.create(constructor.getParameterTypes(), new Object[] { request });
			} else {
				if (constructor.getParameterCount() == 0) {
					bean = constructor.newInstance();
				} else {
					bean = constructor.newInstance(request);
				}
			}
			return (T) bean;
		} catch (Exception e) {
			throw new BeansException(e);
		}
	}

	public void autowrite(Object bean) throws Exception {
		ClassInfo tempClzInfo = classInfo;
		while (tempClzInfo != null) {
			for (FieldInfo field : tempClzInfo.getFieldMap().values()) {
				if (Modifier.isStatic(field.getField().getModifiers())) {
					continue;
				}

				BeanUtils.autoWrite(tempClzInfo.getClz(), beanFactory, propertiesFactory, bean, field);
			}
			tempClzInfo = tempClzInfo.getSuperInfo();
		}
	}

	public void init(Object bean) throws Exception {
		if (initMethods.length != 0) {
			for (Method method : initMethods) {
				method.invoke(bean);
			}
		}
	}

	public void destroy(Object bean) {
		if (destroyMethods.length != 0) {
			for (Method method : destroyMethods) {
				try {
					method.invoke(bean);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String[] getNames() {
		return null;
	}
}
