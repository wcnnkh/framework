package shuchaowen.core.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import shuchaowen.core.beans.annotaion.Autowrite;
import shuchaowen.core.beans.annotaion.Bean;
import shuchaowen.core.beans.annotaion.Config;
import shuchaowen.core.beans.annotaion.Destroy;
import shuchaowen.core.beans.annotaion.InitMethod;
import shuchaowen.core.beans.annotaion.Proxy;
import shuchaowen.core.beans.annotaion.Service;
import shuchaowen.core.beans.annotaion.Transaction;
import shuchaowen.core.beans.exception.BeansException;
import shuchaowen.core.db.DB;
import shuchaowen.core.db.TransactionContext;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.http.server.annotation.Controller;
import shuchaowen.core.util.ClassInfo;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.FieldInfo;
import shuchaowen.core.util.StringUtils;

public final class BeanInfo {
	private final Class<?> type;
	private List<Class<? extends BeanFilter>> beanFilters;
	// 构造函数的参数
	private List<BeanMethodParameter> constructorList;
	private List<BeanProperties> propertiesList;
	private boolean singleton;
	private boolean proxy;
	private Constructor<?> constructor;
	private Class<?>[] constructorParameterTypes;
	
	private List<Method> initMethodList;
	private List<Method> destroyMethodList;
	
	public BeanInfo(Class<?> type){
		if(type.isInterface() || Modifier.isAbstract(type.getModifiers())){
			throw new BeansException(type.getName() + " modifiers error");
		}
		
		this.type = type;
		this.proxy = checkProxy();
		this.beanFilters = getAnnotationBeanFilters();
		this.singleton = isSingletonByAnnoation();
		this.constructor = getConstructor();
		this.initMethodList = getAnnotationInitMethodList();
		this.destroyMethodList = getAnnoationDestroyMethodList();
	}

	public BeanInfo(Class<?> type, List<Class<? extends BeanFilter>> beanFilter, List<BeanMethodParameter> constructorList,
			List<BeanProperties> propertiesList, boolean singleton, String initMethodName, String destoryMethodName) {
		this.type = type;
		this.beanFilters = beanFilter;
		this.constructorList = constructorList;
		this.propertiesList = propertiesList;
		this.singleton = singleton;
		this.proxy = checkProxy();
		this.constructor = getConstructor();
		this.initMethodList = getInitMethodList(initMethodName);
		this.destroyMethodList = getDestroyMethodList(destoryMethodName);
	}
	
	private boolean checkProxy(){
		if(Modifier.isFinal(type.getModifiers())){
			return false;
		}
		return (beanFilters != null && !beanFilters.isEmpty()) || isTransaction();
	}
	
	private boolean isSingletonByAnnoation(){
		Bean bean = type.getAnnotation(Bean.class);
		if(bean == null){
			return true;
		}
		return bean.singleton();
	}
	
	private List<Class<? extends BeanFilter>> getAnnotationBeanFilters(){
		Bean bean = type.getAnnotation(Bean.class);
		return bean == null? new ArrayList<Class<? extends BeanFilter>>():new ArrayList<Class<? extends BeanFilter>>(Arrays.asList(bean.beanFilters()));
	}
	
	private List<Method> getAnnotationInitMethodList(){
		List<Method> methods = new ArrayList<Method>();
		Class<?> tempClz = type;
		while (tempClz != null) {
			for (Method method : tempClz.getDeclaredMethods()) {
				if (Modifier.isStatic(method.getModifiers())) {
					continue;
				}

				InitMethod initMethod = method.getAnnotation(InitMethod.class);
				if (initMethod == null) {
					continue;
				}

				if (method.getParameterCount() != 0) {
					throw new ShuChaoWenRuntimeException("ClassName=" + tempClz.getName() + ",MethodName="
							+ method.getName() + "There must be no parameter.");
				}
				
				method.setAccessible(true);
				methods.add(method);
			}
			tempClz = tempClz.getSuperclass();
		}
		return methods;
	}
	
	private List<Method> getAnnoationDestroyMethodList(){
		List<Method> methods = new ArrayList<Method>();
		Class<?> tempClz = type;
		while (tempClz != null) {
			for (Method method : tempClz.getDeclaredMethods()) {
				if (Modifier.isStatic(method.getModifiers())) {
					continue;
				}

				Destroy destroy = method.getAnnotation(Destroy.class);
				if (destroy == null) {
					continue;
				}

				if (method.getParameterCount() != 0) {
					throw new ShuChaoWenRuntimeException("ClassName=" + tempClz.getName() + ",MethodName="
							+ method.getName() + "There must be no parameter.");
				}
				
				methods.add(method);
			}
			tempClz = tempClz.getSuperclass();
		}
		return methods;
	}

	private List<Method> getInitMethodList(String methodNames) {
		List<Method> list = new ArrayList<Method>();
		String[] names = StringUtils.commonSplit(methodNames);
		for (String name : names) {
			Method method = getMethodByParameterTypes(name);
			if (method != null) {
				method.setAccessible(true);
				list.add(method);
			}
		}
		return list;
	}

	private List<Method> getDestroyMethodList(String methodNames) {
		List<Method> list = new ArrayList<Method>();
		String[] names = StringUtils.commonSplit(methodNames);
		for (String name : names) {
			Method method = getMethodByParameterTypes(name);
			if (method != null) {
				list.add(method);
			}
		}
		return list;
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

	private Constructor<?> getConstructorByParameterTypes(Class<?>... parameterTypes) {
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
		Constructor<?> constructor;
		if (constructorList == null) {
			constructor = getConstructorByParameterTypes();
		}else{
			constructor = getConstructor(type.getConstructors());
			if (constructor == null) {
				constructor = getConstructor(type.getDeclaredConstructors());
			}
		}
		
		if(constructor != null){
			constructorParameterTypes = constructor.getParameterTypes();
		}
		return constructor;
	}

	private Constructor<?> getConstructor(Constructor<?>[] constructors) {
		for (Constructor<?> constructor : constructors) {
			constructor.setAccessible(true);
			if (constructor.getParameterCount() != constructorList.size()) {
				continue;
			}

			boolean find = true;
			String[] paramNames = ClassUtils.getParameterName(constructor);
			for (int i = 0; i < constructorList.size(); i++) {
				Class<?> t = constructor.getParameterTypes()[i];
				BeanMethodParameter beanMethodParameter = constructorList.get(i);
				if (beanMethodParameter.getParameterType() != null) {
					if (!beanMethodParameter.getParameterType().getName().equals(t.getName())) {
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

					if (!b) {
						find = false;
						break;
					}
				}
			}

			if (find) {
				return constructor;
			}

			constructor.setAccessible(false);
		}
		return null;
	}

	private Object createInstance(BeanFactory beanFactory, ConfigFactory configFactory)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (constructorParameterTypes == null || constructorParameterTypes.length == 0) {
			return getConstructor().newInstance();
		} else {
			Object[] args = BeanInfo.getBeanMethodParameterArgs(constructorParameterTypes, constructorList, beanFactory,
					configFactory);
			return constructor.newInstance(args);
		}
	}

	private Object createProxyInstance(BeanFactory beanFactory, ConfigFactory configFactory) {
		Enhancer enhancer = new Enhancer();
		List<BeanFilter> list = null;
		if (beanFilters != null && !beanFilters.isEmpty()) {
			list = new ArrayList<BeanFilter>();
			
			for(Class<? extends BeanFilter> f : beanFilters){
				list.add(beanFactory.get(f));
			}
		}
		
		enhancer.setCallback(new BeanInfoMethodInterceptor(this, list));
		enhancer.setSuperclass(type);
		if (constructorList == null || constructorList.isEmpty()) {
			return enhancer.create();
		} else {
			Object[] args = BeanInfo.getBeanMethodParameterArgs(constructorParameterTypes, constructorList, beanFactory,
					configFactory);
			return enhancer.create(constructorParameterTypes, args);
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

	private Method getMethodByParameterTypes(String methodName, Class<?>... parameterTypes) {
		try {
			return type.getMethod(methodName, parameterTypes);
		} catch (NoSuchMethodException e) {
			try {
				return type.getDeclaredMethod(methodName, parameterTypes);
			} catch (NoSuchMethodException e1) {
			} catch (SecurityException e1) {
			}
		} catch (SecurityException e) {
			try {
				return type.getDeclaredMethod(methodName, parameterTypes);
			} catch (NoSuchMethodException e1) {
			} catch (SecurityException e1) {
			}
		}
		return null;
	}

	private static void setConfig(BeanFactory beanFactory, Class<?> clz, Object obj, Field field) {
		Config config = field.getAnnotation(Config.class);
		if (config != null) {
			FieldInfo fieldInfo = new FieldInfo(clz, field);
			try {
				Object value = beanFactory.get(config.parse()).parse(beanFactory, fieldInfo, config.value(),
						config.charset());
				fieldInfo.set(obj, value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void setBean(BeanFactory beanFactory, Class<?> clz, Object obj, Field field) {
		Autowrite s = field.getAnnotation(Autowrite.class);
		if (s != null) {
			String name = s.name();
			if (name.equals("")) {
				name = field.getType().getName();
			}

			FieldInfo fieldInfo = new FieldInfo(clz, field);
			try {
				fieldInfo.set(obj, beanFactory.get(name));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void setProxy(BeanFactory beanFactory, Class<?> clz, Object obj, Field field){
		Proxy proxy = field.getAnnotation(Proxy.class);
		if (proxy != null) {
			FieldInfo fieldInfo = new FieldInfo(clz, field);
			try {
				fieldInfo.set(obj, (beanFactory.get(proxy.value())).getProxy(beanFactory, field.getType()));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	public static Object[] getBeanMethodParameterArgs(Class<?>[] parameterTypes,
			List<BeanMethodParameter> beanMethodParameters, BeanFactory beanFactory, ConfigFactory configFactory) {
		Object[] args = new Object[parameterTypes.length];
		for (int i = 0; i < args.length; i++) {
			BeanMethodParameter beanConstructorParameter = beanMethodParameters.get(i);
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
	
	public static void autoWriteStatic(Class<?> clz, BeanFactory beanFactory) {
		for (Field field : clz.getDeclaredFields()) {
			if (!Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			
			setBean(beanFactory, clz, null, field);
			setProxy(beanFactory, clz, null, field);
			setConfig(beanFactory, clz, null, field);
		}
	}
	
	public static void initDB(BeanFactory beanFactory, Collection<Class<?>> classList) {
		for (Class<?> clz : classList) {
			Deprecated deprecated = clz.getAnnotation(Deprecated.class);
			if (deprecated != null) {
				continue;
			}

			if (Modifier.isAbstract(clz.getModifiers()) || Modifier.isInterface(clz.getModifiers())) {
				continue;
			}

			if (!Modifier.isPublic(clz.getModifiers())) {
				continue;
			}

			if (!DB.class.isAssignableFrom(clz)) {
				continue;
			}

			beanFactory.get(clz);
		}
	}
	
	public boolean isTransaction(Method method){
		boolean isTransaction = false;
		Controller controller = type.getAnnotation(Controller.class);
		if (controller != null) {
			isTransaction = true;
		}

		Service service = type.getAnnotation(Service.class);
		if (service != null) {
			isTransaction = true;
		}

		Transaction transaction = type.getAnnotation(Transaction.class);
		if (transaction != null) {
			isTransaction = transaction.value();
		}

		Transaction transaction2 = method.getAnnotation(Transaction.class);
		if (transaction2 != null) {
			isTransaction = transaction2.value();
		}
		
		return isTransaction;
	}

	public Class<?> getType() {
		return type;
	}

	public boolean isSingleton() {
		return singleton;
	}

	public boolean isProxy() {
		return proxy;
	}

	public void initMethod(Object obj) {
		if (initMethodList != null && !initMethodList.isEmpty()) {
			for (Method method : initMethodList) {
				try {
					method.invoke(obj);
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

	public void destoryMethod(Object obj) {
		if (destroyMethodList != null && !destroyMethodList.isEmpty()) {
			for (Method method : destroyMethodList) {
				try {
					method.invoke(obj);
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

	public Object newInstance(BeanFactory beanFactory, ConfigFactory configFactory) throws BeansException {
		Object bean;
		try {
			if (isProxy()) {
				bean = createProxyInstance(beanFactory, configFactory);
			} else {
				bean = createInstance(beanFactory, configFactory);
			}
			setProperties(beanFactory, configFactory, bean);
			return bean;
		} catch (Exception e) {
			throw new BeansException(e);
		}
	}
	
	public void wrapper(Object obj, BeanFactory beanFactory, ConfigFactory configFactory){
		Class<?> tempClz = type;
		while (tempClz != null) {
			for (Field field : tempClz.getDeclaredFields()) {
				if (Modifier.isStatic(field.getModifiers())) {
					continue;
				}
				
				setBean(beanFactory, tempClz, obj, field);
				setProxy(beanFactory, tempClz, obj, field);
				setConfig(beanFactory, tempClz, obj, field);
			}
			tempClz = tempClz.getSuperclass();
		}
		initMethod(obj);
	}
}

class BeanInfoMethodInterceptor implements MethodInterceptor {
	private BeanInfo beanInfo;
	private List<BeanFilter> beanFilters;
	
	public BeanInfoMethodInterceptor(BeanInfo beanInfo, List<BeanFilter> beanFilters) {
		this.beanInfo = beanInfo;
		this.beanFilters = beanFilters;
	}

	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		boolean isTransaction = beanInfo.isTransaction(method);
		if (isTransaction) {
			TransactionContext.getInstance().begin();
			try {
				BeanFilterChain beanFilterChain = new BeanFilterChain(beanFilters);
				return beanFilterChain.doFilter(obj, method, args, proxy);
			} catch (Throwable e) {
				throw e;
			} finally {
				TransactionContext.getInstance().commit();
			}
		} else {
			BeanFilterChain beanFilterChain = new BeanFilterChain(beanFilters);
			return beanFilterChain.doFilter(obj, method, args, proxy);
		}
	}
}
