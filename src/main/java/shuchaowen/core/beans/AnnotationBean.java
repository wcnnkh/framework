package shuchaowen.core.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.cglib.proxy.Enhancer;
import shuchaowen.core.beans.annotaion.Destroy;
import shuchaowen.core.beans.annotaion.InitMethod;
import shuchaowen.core.beans.annotaion.Retry;
import shuchaowen.core.beans.exception.BeansException;
import shuchaowen.core.util.ClassInfo;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.FieldInfo;
import shuchaowen.core.util.StringUtils;

public class AnnotationBean implements Bean {
	private final BeanFactory beanFactory;
	private final Class<?> type;
	private final String id;
	private final boolean singleton;
	private final List<Class<? extends BeanFilter>> beanFilters;
	private Constructor<?> constructor;
	private final List<Method> initMethodList = new ArrayList<Method>();
	private final List<Method> destroyMethodList = new ArrayList<Method>();
	private final boolean proxy;
	private String[] names;
	private String factoryMethodName;
	private Method factoryMethod;
	private Enhancer enhancer;

	public AnnotationBean(BeanFactory beanFactory, Class<?> type) throws Exception {
		this.beanFactory = beanFactory;
		this.type = type;

		shuchaowen.core.beans.annotaion.Bean bean = type.getAnnotation(shuchaowen.core.beans.annotaion.Bean.class);
		if (bean != null) {
			this.id = StringUtils.isNull(bean.id()) ? ClassUtils.getCGLIBRealClassName(type) : bean.id();
			this.singleton = bean.singleton();
			this.beanFilters = Arrays.asList(bean.beanFilters());
			this.names = bean.names();
			this.factoryMethodName = bean.factoryMethod();
		} else {
			this.id = ClassUtils.getCGLIBRealClassName(type);
			this.singleton = true;
			this.beanFilters = null;
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

		this.proxy = checkProxy();
	}
	
	public static List<BeanMethod> getInitMethodList(Class<?> type){
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
	
	public static List<BeanMethod> getDestroyMethdoList(Class<?> type){
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
	
	public static Retry getRetry(Class<?> type, Method method){
		Retry retry = method.getAnnotation(Retry.class);
		if(retry == null){
			retry = type.getAnnotation(Retry.class);
		}
		return retry;
	}

	private boolean checkProxy() {
		if (Modifier.isFinal(type.getModifiers())) {
			return false;
		}

		if (beanFilters != null && !beanFilters.isEmpty()) {
			return true;
		}
		
		for (Method method : type.getDeclaredMethods()) {
			if(BeanUtils.isTransaction(type, method)){
				return true;
			}
			
			Retry retry = getRetry(type, method);
			if(retry != null && retry.errors().length != 0){
				return true;
			}
		}
		return false;
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
		if(enhancer == null){
			List<String> filters = new ArrayList<String>();
			if (beanFilters != null && !beanFilters.isEmpty()) {
				filters = new ArrayList<String>();

				for (Class<? extends BeanFilter> f : beanFilters) {
					filters.add(f.getName());
				}
			}
			enhancer = BeanUtils.getEnhancer(type, filters, beanFactory);
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

	public void autowrite(Object bean) {
		ClassInfo classInfo = ClassUtils.getClassInfo(type);
		while (classInfo != null) {
			for (FieldInfo field : classInfo.getFieldMap().values()) {
				if (Modifier.isStatic(field.getField().getModifiers())) {
					continue;
				}

				BeanUtils.setBean(beanFactory, classInfo.getClz(), bean, field);
				BeanUtils.setProxy(beanFactory, classInfo.getClz(), bean, field);
				BeanUtils.setConfig(beanFactory, classInfo.getClz(), bean, field);
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
