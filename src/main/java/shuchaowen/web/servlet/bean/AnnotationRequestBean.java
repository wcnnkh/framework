package shuchaowen.web.servlet.bean;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import net.sf.cglib.proxy.Enhancer;
import shuchaowen.beans.AnnotationBean;
import shuchaowen.beans.BeanFactory;
import shuchaowen.beans.BeanUtils;
import shuchaowen.beans.annotaion.Destroy;
import shuchaowen.beans.annotaion.InitMethod;
import shuchaowen.beans.property.PropertiesFactory;
import shuchaowen.common.ClassInfo;
import shuchaowen.common.FieldInfo;
import shuchaowen.common.exception.BeansException;
import shuchaowen.common.utils.ClassUtils;
import shuchaowen.web.servlet.Request;

public final class AnnotationRequestBean implements RequestBean {
	private final BeanFactory beanFactory;
	private final ClassInfo classInfo;
	private final Class<?> type;
	private final String id;
	private Constructor<?> constructor;
	private final List<Method> initMethodList = new ArrayList<Method>();
	private final List<Method> destroyMethodList = new ArrayList<Method>();
	private final boolean proxy;
	private Enhancer enhancer;
	private final PropertiesFactory propertiesFactory;

	public AnnotationRequestBean(BeanFactory beanFactory,
			PropertiesFactory propertiesFactory, Class<?> type)
			throws Exception {
		this.beanFactory = beanFactory;
		this.type = type;
		this.classInfo = ClassUtils.getClassInfo(type);
		this.propertiesFactory = propertiesFactory;
		this.id = ClassUtils.getProxyRealClassName(type);

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

		this.proxy = AnnotationBean.checkProxy(type);

		this.constructor = getAnnotationRequestBeanConstructor(type);
		if (this.constructor == null) {
			throw new BeansException("not found constructor");
		}
		enhancer = classInfo.createEnhancer(null, null);
	}
	
	public static Constructor<?> getAnnotationRequestBeanConstructor(Class<?> type){
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
						: enhancer.create(constructor.getParameterTypes(),
								new Object[] { request });
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
		ClassInfo classInfo = ClassUtils.getClassInfo(type);
		while (classInfo != null) {
			for (FieldInfo field : classInfo.getFieldMap().values()) {
				if (Modifier.isStatic(field.getField().getModifiers())) {
					continue;
				}

				BeanUtils.autoWrite(classInfo.getClz(), beanFactory,
						propertiesFactory, bean, field);
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
		return null;
	}
}
