package shuchaowen.beans;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import shuchaowen.beans.annotaion.Destroy;
import shuchaowen.beans.annotaion.InitMethod;
import shuchaowen.beans.annotaion.Retry;
import shuchaowen.common.exception.NotSupportException;
import shuchaowen.common.utils.ClassUtils;
import shuchaowen.core.util.StringUtils;
import shuchaowen.db.annoation.Table;

public abstract class AbstractInterfaceProxyBean implements Bean {
	private final Class<?> type;
	private final String id;
	private final List<Method> initMethodList = new ArrayList<Method>();
	private final List<Method> destroyMethodList = new ArrayList<Method>();
	private String[] names;

	public AbstractInterfaceProxyBean(Class<?> type) throws Exception {
		this.type = type;
		shuchaowen.beans.annotaion.Bean bean = type.getAnnotation(shuchaowen.beans.annotaion.Bean.class);
		if (bean != null) {
			this.id = StringUtils.isNull(bean.id()) ? ClassUtils.getCGLIBRealClassName(type) : bean.id();
			this.names = bean.names();
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
		return true;
	}

	public boolean isProxy() {
		return true;
	}

	public String getId() {
		return this.id;
	}

	public Class<?> getType() {
		return this.type;
	}

	public <T> T newInstance(Class<?>[] parameterTypes, Object... args) {
		throw new NotSupportException(getType().getName());
	}

	public void autowrite(Object bean) throws Exception {
		// ignore
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
