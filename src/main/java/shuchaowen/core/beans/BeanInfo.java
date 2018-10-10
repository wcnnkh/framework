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

public final class BeanInfo {
	private final Class<?> type;
	private final List<BeanFilter> beanFilters;
	// 构造函数的参数
	private final List<BeanMethodParameter> constructorList;
	private final List<BeanProperties> propertiesList;
	private final boolean singleton;
	private final boolean proxy;
	private final Constructor<?> constructor;
	private final Class<?>[] constructorParameterTypes;
	
	public BeanInfo(Class<?> type, List<BeanFilter> beanFilter,
			List<BeanMethodParameter> constructorList,
			List<BeanProperties> propertiesList, boolean singleton) {
		this.type = type;
		this.beanFilters = beanFilter;
		this.constructorList = constructorList;
		this.propertiesList = propertiesList;
		this.proxy = (beanFilters != null && !beanFilters.isEmpty())
				|| isTransaction();
		this.singleton = singleton;
		this.constructor = getConstructor();
		this.constructorParameterTypes = constructor.getParameterTypes();
	}

	public boolean isSingleton() {
		return singleton;
	}

	public boolean isProxy() {
		return proxy;
	}

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

	private Constructor<?> getConstructorByParameterTypes(
			Class<?>... parameterTypes) {
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

	private Constructor<?> getConstructor() {
		if (constructorList == null) {
			return getConstructorByParameterTypes();
		}
		
		Constructor<?> constructor = getConstractor(type.getConstructors());
		if(constructor == null){
			constructor = getConstractor(type.getDeclaredConstructors());
		}
		return constructor;
	}

	private Constructor<?> getConstractor(Constructor<?>[] constructors) {
		for (Constructor<?> constructor : constructors) {
			constructor.setAccessible(true);
			if (constructor.getParameterCount() != constructorList.size()) {
				continue;
			}

			boolean find = true;
			String[] paramNames = ClassUtils.getParameterName(constructor);
			for (int i = 0; i < constructorList.size(); i++) {
				Class<?> t = constructor.getParameterTypes()[i];
				BeanMethodParameter beanMethodParameter = constructorList
						.get(i);
				if (beanMethodParameter.getParameterType() != null) {
					if(!beanMethodParameter.getParameterType().getName()
							.equals(t.getName())){
						find = false;
						break;
					}
				} else {
					boolean b = false;
					String name = paramNames[i];
					for (BeanMethodParameter p : constructorList) {
						if (name.equals(p.getName())) {
							b = true;
						}
					}
					
					if(!b){
						find = false;
						break;
					}
				}
			}
			
			if(find){
				return constructor;
			}
			
			constructor.setAccessible(false);
		}
		return null;
	}

	private Object[] getConstructorArgs(BeanFactory beanFactory,
			ConfigFactory configFactory) {
		Object[] args = new Object[constructorParameterTypes.length];
		for (int i = 0; i < args.length; i++) {
			BeanMethodParameter beanConstructorParameter = constructorList
					.get(i);
			switch (beanConstructorParameter.getType()) {
			case value:
				args[i] = StringUtils.conversion(
						beanConstructorParameter.getValue(), constructorParameterTypes[i]);
				break;
			case ref:
				args[i] = beanFactory.get(beanConstructorParameter.getValue());
				break;
			case config:
				args[i] = StringUtils.conversion(configFactory
						.getPropertie(beanConstructorParameter.getValue()),
						constructorParameterTypes[i]);
				break;
			default:
				break;
			}
		}
		return args;
	}

	private Object createInstance(BeanFactory beanFactory,
			ConfigFactory configFactory) throws InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		if (constructorParameterTypes == null || constructorParameterTypes.length == 0) {
			return getConstructor().newInstance();
		} else {
			return constructor.newInstance(getConstructorArgs(beanFactory,
					configFactory));
		}
	}

	private Object createProxyInstance(BeanFactory beanFactory,
			ConfigFactory configFactory) {
		Enhancer enhancer = new Enhancer();
		if (beanFilters != null && !beanFilters.isEmpty()) {
			enhancer.setCallback(new BeanFilterMethodInterceptor(beanFilters));
		}

		enhancer.setSuperclass(type);
		if (constructorList == null || constructorList.isEmpty()) {
			return enhancer.create();
		} else {
			return enhancer.create(constructorParameterTypes,
					getConstructorArgs(beanFactory, configFactory));
		}
	}

	private void setProperties(BeanFactory beanFactory,
			ConfigFactory configFactory, Object bean)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		if (propertiesList == null || propertiesList.isEmpty()) {
			return;
		}

		for (BeanProperties beanProperties : propertiesList) {
			ClassInfo classInfo = new ClassInfo(type);
			FieldInfo fieldInfo = classInfo.getFieldInfo(beanProperties
					.getName());
			if (fieldInfo != null) {
				Object value = null;
				switch (beanProperties.getType()) {
				case value:
					value = StringUtils.conversion(beanProperties.getValue(),
							fieldInfo.getType());
					break;
				case ref:
					value = beanFactory.get(beanProperties.getValue());
					break;
				case config:
					value = StringUtils.conversion(configFactory
							.getPropertie(beanProperties.getValue()), fieldInfo
							.getType());
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

	public Object newInstance(BeanFactory beanFactory,
			ConfigFactory configFactory) throws BeansException {
		Object bean;
		try {
			if (isProxy()) {
				bean = createInstance(beanFactory, configFactory);
			} else {
				bean = createProxyInstance(beanFactory, configFactory);
			}

			setProperties(beanFactory, configFactory, bean);

			// autowrite
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

	public Object intercept(Object obj, Method method, Object[] args,
			MethodProxy proxy) throws Throwable {
		BeanFilterChain beanFilterChain = new BeanFilterChain(beanFilters);
		return beanFilterChain.doFilter(obj, method, args, proxy);
	}

}
