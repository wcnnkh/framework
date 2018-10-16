package shuchaowen.core.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.cglib.proxy.Enhancer;
import shuchaowen.core.beans.annotaion.Destroy;
import shuchaowen.core.beans.annotaion.InitMethod;
import shuchaowen.core.beans.annotaion.Service;
import shuchaowen.core.beans.annotaion.Transaction;
import shuchaowen.core.beans.exception.BeansException;
import shuchaowen.core.http.server.annotation.Controller;
import shuchaowen.core.util.ClassUtils;
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

	public AnnotationBean(BeanFactory beanFactory, Class<?> type) throws Exception {
		this.beanFactory = beanFactory;
		this.type = type;
		
		shuchaowen.core.beans.annotaion.Bean bean = type.getAnnotation(shuchaowen.core.beans.annotaion.Bean.class);
		if(bean != null){
			this.id = StringUtils.isNull(bean.id()) ? ClassUtils.getCGLIBRealClassName(type) : bean.id();
			this.singleton = bean.singleton();
			this.beanFilters = Arrays.asList(bean.beanFilters());
		}else{
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

	private boolean checkProxy() {
		if (Modifier.isFinal(type.getModifiers())) {
			return false;
		}

		if (beanFilters != null && !beanFilters.isEmpty()) {
			return true;
		}

		Controller controller = type.getAnnotation(Controller.class);
		if (controller != null) {
			return true;
		}

		Service service = type.getAnnotation(Service.class);
		if (service != null) {
			return true;
		}

		Transaction transaction = type.getAnnotation(Transaction.class);
		if (transaction != null) {
			return true;
		}

		for (Method method : type.getDeclaredMethods()) {
			Transaction t = method.getAnnotation(Transaction.class);
			if (t != null) {
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
	
	private Enhancer getProxyEnhancer(){
		Enhancer enhancer = new Enhancer();
		List<BeanFilter> list = null;
		if (beanFilters != null && !beanFilters.isEmpty()) {
			list = new ArrayList<BeanFilter>();
			
			for(Class<? extends BeanFilter> f : beanFilters){
				list.add(beanFactory.get(f));
			}
		}
		
		enhancer.setCallback(new BeanMethodInterceptor(type, list));
		enhancer.setSuperclass(type);
		return enhancer;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T newInstance() {
		if(constructor == null){
			try {
				this.constructor = type.getDeclaredConstructor();//不用考虑并发
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
			return (T) bean;
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
			return (T) bean;
		} catch (Exception e) {
			throw new BeansException(e);
		}
	}

	public void wrapper(Object bean) {
		Class<?> tempClz = type;
		while (tempClz != null) {
			for (Field field : tempClz.getDeclaredFields()) {
				if (Modifier.isStatic(field.getModifiers())) {
					continue;
				}
				
				BeanUtils.setBean(beanFactory, tempClz, bean, field);
				BeanUtils.setProxy(beanFactory, tempClz, bean, field);
				BeanUtils.setConfig(beanFactory, tempClz, bean, field);
			}
			tempClz = tempClz.getSuperclass();
		}
		
		if (initMethodList != null && !initMethodList.isEmpty()) {
			for (Method method : initMethodList) {
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
}
