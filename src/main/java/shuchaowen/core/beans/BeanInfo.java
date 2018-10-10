package shuchaowen.core.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import shuchaowen.core.beans.annotaion.Service;
import shuchaowen.core.beans.annotaion.Transaction;
import shuchaowen.core.beans.excepation.BeansException;
import shuchaowen.core.http.server.annotation.Controller;
import shuchaowen.core.util.ClassInfo;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.FieldInfo;
import shuchaowen.core.util.StringUtils;

public class BeanInfo {
	private Class<?> type;
	private List<BeanFilter> beanFilters;
	// 构造函数的参数
	private List<BeanConstructorParameter> constructorList;
	private List<BeanProperties> propertiesList;

	private boolean isTransaction() {
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

	/**
	 * 是否启用代理
	 * 
	 * @return
	 */
	private boolean isProxy() {
		if (beanFilters != null && !beanFilters.isEmpty()) {
			return true;
		}

		return isTransaction();
	}

	private Constructor<?> getConstructor(Class<?>... parameterTypes) {
		try {
			return type.getConstructor(parameterTypes);
		} catch (NoSuchMethodException e) {
			try {
				return type.getDeclaredConstructor(parameterTypes);
			} catch (NoSuchMethodException e1) {
			} catch (SecurityException e1) {
			}
		} catch (SecurityException e) {
			try {
				return type.getDeclaredConstructor(parameterTypes);
			} catch (NoSuchMethodException e1) {
			} catch (SecurityException e1) {
			}
		}
		return null;
	}

	private Class<?> getParameterType(Constructor<?> constructor, String name) {
		Class<?>[] types = constructor.getParameterTypes();
		String[] paramNames = ClassUtils.getParameterName(constructor);
		for (int i = 0; i < paramNames.length; i++) {
			if (paramNames.equals(name)) {
				return types[i];
			}
		}
		throw new BeansException("not found parameters [" + name + "]");
	}

	private Class<?>[] getParameterTypes() {
		if (constructorList == null) {
			return null;
		}

		for (Constructor<?> constructor : type.getConstructors()) {
			constructor.setAccessible(true);
			if (constructor.getParameterCount() == constructorList.size()) {
				Class<?>[] types = new Class<?>[constructorList.size()];
				for (int i = 0; i < constructorList.size(); i++) {
					BeanConstructorParameter beanConstructorParameter = constructorList.get(i);
					if (beanConstructorParameter.getParameterType() == null) {
						if (StringUtils.isNull(true, beanConstructorParameter.getName())) {
							types[i] = constructor.getParameterTypes()[i];
						} else {
							types[i] = getParameterType(constructor, beanConstructorParameter.getName());
						}
					} else {
						types[i] = beanConstructorParameter.getParameterType();
					}
				}
			}
			constructor.setAccessible(false);
		}
		throw new BeansException("not found constructor [" + type.getName() + "]");
	}

	private Object[] getConstructorArgs(BeanFactory beanFactory, ConfigFactory configFactory, Class<?>[] parameterTypes) {
		Object[] args = new Object[parameterTypes.length];
		for (int i = 0; i < args.length; i++) {
			BeanConstructorParameter beanConstructorParameter = constructorList.get(i);
			switch (beanConstructorParameter.getType()) {
			case value:
				args[i] = StringUtils.conversion(beanConstructorParameter.getValue(), parameterTypes[i]);
				break;
			case ref:
				args[i] = beanFactory.get(beanConstructorParameter.getValue());
				break;
			case config:
				args[i] = StringUtils.conversion(configFactory.getPropertie(beanConstructorParameter.getValue()),
						parameterTypes[i]);
				break;
			default:
				break;
			}
		}
		return args;
	}

	private Object createInstance(BeanFactory beanFactory, ConfigFactory configFactory)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Class<?>[] types = getParameterTypes();
		if (types == null || types.length == 0) {
			return getConstructor().newInstance();
		} else {
			Constructor<?> constructor = getConstructor(types);
			return constructor.newInstance(getConstructorArgs(beanFactory, configFactory, types));
		}
	}

	private Object createProxyInstance(BeanFactory beanFactory, ConfigFactory configFactory) {
		Enhancer enhancer = new Enhancer();
		if (beanFilters != null && !beanFilters.isEmpty()) {
			enhancer.setCallback(new BeanFilterMethodInterceptor(beanFilters));
		}

		enhancer.setSuperclass(type);
		if (constructorList == null || constructorList.isEmpty()) {
			return enhancer.create();
		} else {
			Class<?>[] types = getParameterTypes();
			return enhancer.create(types, getConstructorArgs(beanFactory, configFactory, types));
		}
	}

	private void setProperties(BeanFactory beanFactory, ConfigFactory configFactory, Object bean)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if (propertiesList == null || propertiesList.isEmpty()) {
			return;
		}

		for (BeanProperties beanProperties : propertiesList) {
			ClassInfo classInfo = new ClassInfo(type);
			FieldInfo fieldInfo = classInfo.getFieldInfo(beanProperties.getName());
			if (fieldInfo != null) {
				Object value = null;
				switch (beanProperties.getType()) {
				case value:
					value = StringUtils.conversion(beanProperties.getValue(), fieldInfo.getType());
					break;
				case ref:
					value = beanFactory.get(beanProperties.getValue());
					break;
				case config:
					value = StringUtils.conversion(configFactory.getPropertie(beanProperties.getValue()),
							fieldInfo.getType());
					break;
				default:
					break;
				}

				if (beanProperties.isSetter()) {
					fieldInfo.set(bean, value);
				} else {
					fieldInfo.forceSet(bean, value);
				}
			}
		}
	}

	public Object newInstance(BeanFactory beanFactory, ConfigFactory configFactory) throws BeansException {
		Object bean;
		try {
			if (isProxy()) {
				bean = createInstance(beanFactory, configFactory);
			} else {
				bean = createProxyInstance(beanFactory, configFactory);
			}
			
			setProperties(beanFactory, configFactory, bean);
			
			//autowrite
			
			return bean;
		} catch (Exception e) {
			throw new BeansException(e);
		}
	}
}

class BeanFilterMethodInterceptor implements MethodInterceptor {
	private List<BeanFilter> beanFilters;

	public BeanFilterMethodInterceptor(List<BeanFilter> beanFilters) {
		this.beanFilters = beanFilters;
	}

	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		BeanFilterChain beanFilterChain = new BeanFilterChain(beanFilters);
		return beanFilterChain.doFilter(obj, method, args, proxy);
	}

}
