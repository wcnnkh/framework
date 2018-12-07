package shuchaowen.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import net.sf.cglib.proxy.Enhancer;
import shuchaowen.beans.annotaion.Destroy;
import shuchaowen.beans.annotaion.InitMethod;
import shuchaowen.beans.annotaion.Retry;
import shuchaowen.beans.property.PropertiesFactory;
import shuchaowen.common.exception.BeansException;
import shuchaowen.core.util.StringUtils;
import shuchaowen.db.annoation.Table;
import shuchaowen.reflect.ClassInfo;
import shuchaowen.reflect.ClassUtils;
import shuchaowen.reflect.FieldInfo;

public class AnnotationBean implements Bean {
	private final BeanFactory beanFactory;
	private final ClassInfo classInfo;
	private final Class<?> type;
	private final String id;
	private final boolean singleton;
	private Constructor<?> constructor;
	private final List<Method> initMethodList = new ArrayList<Method>();
	private final List<Method> destroyMethodList = new ArrayList<Method>();
	private final boolean proxy;
	private String[] names;
	private String factoryMethodName;
	private Method factoryMethod;
	private Enhancer enhancer;
	private final PropertiesFactory propertiesFactory;

	public AnnotationBean(BeanFactory beanFactory, PropertiesFactory propertiesFactory, Class<?> type) throws Exception {
		this.beanFactory = beanFactory;
		this.type = type;
		this.classInfo = ClassUtils.getClassInfo(type);
		this.singleton = isSignleton(type);
		this.propertiesFactory = propertiesFactory;
		
		shuchaowen.beans.annotaion.Bean bean = type.getAnnotation(shuchaowen.beans.annotaion.Bean.class);
		if (bean != null) {
			this.id = StringUtils.isNull(bean.id()) ? ClassUtils.getCGLIBRealClassName(type) : bean.id();
			this.names = bean.names();
			this.factoryMethodName = bean.factoryMethod();
		} else {
			this.id = ClassUtils.getCGLIBRealClassName(type);
		}

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

		this.proxy = checkProxy(type);
	}

	public static List<BeanMethod> getInitMethodList(Class<?> type) {
		List<BeanMethod> list = new ArrayList<BeanMethod>();
		Class<?> tempClz = type;
		for (Method method : tempClz.getDeclaredMethods()) {
			if (Modifier.isStatic(method.getModifiers())) {
				continue;
			}

			InitMethod initMethod = method.getAnnotation(InitMethod.class);
			if (initMethod != null) {
				method.setAccessible(true);
				list.add(new NoArgumentBeanMethod(method));
			}
		}
		return list;
	}

	public static List<BeanMethod> getDestroyMethdoList(Class<?> type) {
		List<BeanMethod> list = new ArrayList<BeanMethod>();
		Class<?> tempClz = type;
		for (Method method : tempClz.getDeclaredMethods()) {
			if (Modifier.isStatic(method.getModifiers())) {
				continue;
			}

			Destroy destroy = method.getAnnotation(Destroy.class);
			if (destroy != null) {
				method.setAccessible(true);
				list.add(new NoArgumentBeanMethod(method));
			}
		}
		return list;
	}

	public static Retry getRetry(Class<?> type, Method method) {
		Retry retry = method.getAnnotation(Retry.class);
		if (retry == null) {
			retry = type.getAnnotation(Retry.class);
		}
		return retry;
	}

	public static boolean checkProxy(Class<?> type) {
		if (Modifier.isFinal(type.getModifiers())) {
			return false;
		}

		for (Method method : type.getDeclaredMethods()) {
			if (BeanUtils.isTransaction(type, method)) {
				return true;
			}

			Retry retry = getRetry(type, method);
			if (retry != null && retry.errors().length != 0) {
				return true;
			}
		}
		return false;
	}

	public static boolean isSignleton(Class<?> type) {
		shuchaowen.beans.annotaion.Bean bean = type.getAnnotation(shuchaowen.beans.annotaion.Bean.class);
		boolean b = true;
		if (bean != null) {
			b = bean.singleton();
		}

		if (b) {
			Table table = type.getAnnotation(Table.class);
			b = table == null;
		}
		return b;
	}

	public boolean isSingleton() {
		return this.singleton;
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
			enhancer = classInfo.createEnhancer(null, null);
		}
		return enhancer;
	}

	@SuppressWarnings("unchecked")
	public <T> T newInstance() {
		if (constructor == null) {
			try {
				this.constructor = type.getDeclaredConstructor();// 不用考虑并发

				if (factoryMethod == null) {
					if (!StringUtils.isNull(factoryMethodName)) {
						factoryMethod = type.getMethod(factoryMethodName);
					}
				}
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
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

			return (T) (factoryMethod == null ? bean
					: factoryMethod.invoke(Modifier.isStatic(factoryMethod.getModifiers()) ? null : bean));
		} catch (Exception e) {
			throw new BeansException(e);
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

			return (T) (factoryMethod == null ? bean
					: factoryMethod.invoke(Modifier.isStatic(factoryMethod.getModifiers()) ? null : bean));
		} catch (Exception e) {
			throw new BeansException(e);
		}
	}

	public void autowrite(Object bean) throws Exception {
		ClassInfo classInfo = ClassUtils.getClassInfo(type);
		while (classInfo != null) {
			for (FieldInfo field : classInfo.getFieldMap().values()) {
				if (Modifier.isStatic(field.getField().getModifiers())) {
					continue;
				}
				
				BeanUtils.autoWrite(classInfo.getClz(), beanFactory, propertiesFactory, bean, field);
			}
			classInfo = classInfo.getSuperInfo();
		}
	}

	public void init(Object bean) throws Exception {
		if (initMethodList != null && !initMethodList.isEmpty()) {
			for (Method method : initMethodList) {
				method.invoke(bean);
			}
		}
	}

	public void destroy(Object bean) {
		if (destroyMethodList != null && !destroyMethodList.isEmpty()) {
			for (Method method : destroyMethodList) {
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
		return names;
	}
}
