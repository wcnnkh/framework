package shuchaowen.core.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;

import shuchaowen.core.beans.annotaion.Autowrite;
import shuchaowen.core.beans.annotaion.Config;
import shuchaowen.core.beans.annotaion.Destroy;
import shuchaowen.core.beans.annotaion.InitMethod;
import shuchaowen.core.beans.annotaion.Properties;
import shuchaowen.core.beans.annotaion.Proxy;
import shuchaowen.core.beans.annotaion.Service;
import shuchaowen.core.beans.annotaion.Transaction;
import shuchaowen.core.db.DB;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.http.server.annotation.Controller;
import shuchaowen.core.invoke.Invoker;
import shuchaowen.core.invoke.ReflectInvoker;
import shuchaowen.core.util.ClassInfo;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.FieldInfo;
import shuchaowen.core.util.Logger;
import shuchaowen.core.util.StringUtils;

public final class BeanUtils {
	private BeanUtils(){};
	
	/**
	 * 调用init方法
	 * 
	 * @param beanFactory
	 * @param classList
	 * @throws Exception
	 */
	private static void invokerInitStaticMethod(Collection<Class<?>> classList) throws Exception {
		List<Invoker> list = new ArrayList<Invoker>();
		for (Class<?> clz : classList) {
			for (Method method : clz.getDeclaredMethods()) {
				if (!Modifier.isStatic(method.getModifiers())) {
					continue;
				}

				InitMethod initMethod = method.getAnnotation(InitMethod.class);
				if (initMethod == null) {
					continue;
				}

				if (method.getParameterCount() != 0) {
					throw new ShuChaoWenRuntimeException("ClassName=" + clz.getName() + ",MethodName="
							+ method.getName() + "There must be no parameter.");
				}

				Invoker invoke = new ReflectInvoker(null, method);
				list.add(invoke);
			}
		}

		// 调用指定注解的方法
		CountDownLatch countDownLatch = new CountDownLatch(list.size());
		for (Invoker info : list) {
			InitProcess process = new InitProcess(info, countDownLatch);
			new Thread(process).start();
		}
		countDownLatch.await();
	}

	public static void destroyStaticMethod(Collection<Class<?>> classList) throws Exception {
		List<Invoker> list = new ArrayList<Invoker>();
		for (Class<?> clz : classList) {
			for (Method method : clz.getDeclaredMethods()) {
				if (!Modifier.isStatic(method.getModifiers())) {
					continue;
				}

				Destroy destroy = method.getAnnotation(Destroy.class);
				if (destroy == null) {
					continue;
				}

				if (method.getParameterCount() != 0) {
					throw new ShuChaoWenRuntimeException("ClassName=" + clz.getName() + ",MethodName="
							+ method.getName() + "There must be no parameter.");
				}

				Invoker invoke = new ReflectInvoker(null, method);
				list.add(invoke);
			}
		}

		CountDownLatch countDownLatch = new CountDownLatch(list.size());
		for (Invoker info : list) {
			InitProcess process = new InitProcess(info, countDownLatch);
			new Thread(process).start();
		}
		countDownLatch.await();
	}

	public static void initStatic(BeanFactory beanFactory, Collection<Class<?>> classList) throws Exception {
		initAutowriteStatic(beanFactory, classList);
		invokerInitStaticMethod(classList);
		initDB(beanFactory, classList);
	}

	private static void autoWriteStatic(Class<?> clz, BeanFactory beanFactory) throws Exception {
		ClassInfo classInfo= ClassUtils.getClassInfo(clz);
		for(Entry<String, FieldInfo> entry : classInfo.getFieldMap().entrySet()){
			FieldInfo field = entry.getValue();
			if (!Modifier.isStatic(field.getField().getModifiers())) {
				continue;
			}

			setBean(beanFactory, clz, null, field);
			setProxy(beanFactory, clz, null, field);
			setConfig(beanFactory, clz, null, field);
		}
	}

	/**
	 * 过滤非静态方法和非静态字段
	 * 
	 * @param classList
	 */
	private static void initAutowriteStatic(BeanFactory beanFactory, Collection<Class<?>> classList) throws Exception {
		for (Class<?> clz : classList) {
			autoWriteStatic(clz, beanFactory);
		}
	}

	public static void initDB(BeanFactory beanFactory, Collection<Class<?>> classList) {
		for (Class<?> clz : classList) {
			Deprecated deprecated = clz.getAnnotation(Deprecated.class);
			if (deprecated != null) {
				continue;
			}

			if (!DB.class.isAssignableFrom(clz)) {
				continue;
			}

			if (Modifier.isAbstract(clz.getModifiers()) || Modifier.isInterface(clz.getModifiers())) {
				continue;
			}

			if (!Modifier.isPublic(clz.getModifiers())) {
				continue;
			}

			beanFactory.get(clz);
		}
	}

	/**
	 * 对参数重新排序
	 * 
	 * @param executable
	 * @param beanMethodParameters
	 * @return
	 */
	public static BeanParameter[] sortParameters(Executable executable,
			List<BeanParameter> beanMethodParameters) {
		if (executable.getParameterCount() != beanMethodParameters.size()) {
			return null;
		}

		String[] paramNames;
		if (executable instanceof Constructor) {
			paramNames = ClassUtils.getParameterName((Constructor<?>) executable);
		} else {
			paramNames = ClassUtils.getParameterName((Method) executable);
		}

		BeanParameter[] methodParameters = new BeanParameter[beanMethodParameters.size()];
		Class<?>[] oldTypes = executable.getParameterTypes();
		Class<?>[] types = new Class<?>[beanMethodParameters.size()];
		for (int i = 0; i < beanMethodParameters.size(); i++) {
			BeanParameter beanMethodParameter = beanMethodParameters.get(i).clone();
			if (!StringUtils.isNull(beanMethodParameter.getName())) {
				for (int a = 0; a < paramNames.length; a++) {
					if (paramNames[a].equals(beanMethodParameter.getName())) {
						types[a] = oldTypes[a];
						methodParameters[a] = beanMethodParameters.get(i).clone();
						methodParameters[a].setParameterType(oldTypes[a]);
					}
				}
			} else if (beanMethodParameter.getParameterType() != null) {
				methodParameters[i] = beanMethodParameter;
				types[i] = beanMethodParameter.getParameterType();
			} else {
				types[i] = oldTypes[i];
				methodParameters[i] = beanMethodParameter;
				methodParameters[i].setParameterType(types[i]);
			}

		}

		boolean find = true;
		for (int b = 0; b < types.length; b++) {
			if (oldTypes[b] != types[b]) {
				find = false;
				break;
			}
		}
		return find ? methodParameters : null;
	}

	public static Object[] getBeanMethodParameterArgs(BeanParameter[] beanParameters,
			BeanFactory beanFactory, PropertiesFactory propertiesFactory) throws Exception {
		Object[] args = new Object[beanParameters.length];
		for (int i = 0; i < args.length; i++) {
			BeanParameter beanParameter = beanParameters[i];
			args[i] = beanParameter.parseValue(beanFactory, propertiesFactory);
		}
		return args;
	}
	
	public static boolean isTransaction(Class<?> type, Method method) {
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

	public static void setConfig(BeanFactory beanFactory, Class<?> clz, Object obj, FieldInfo field) throws Exception {
		Config config = field.getField().getAnnotation(Config.class);
		if (config != null) {
			if (Modifier.isStatic(field.getField().getModifiers())) {
				Logger.warn("@Config",
						"class[" + clz.getName() + "] fieldName[" + field.getName() + "] is a static field");
			}

			Object value = null;
				if (field.forceGet(obj) != null) {
					Logger.warn("@Config",
							"class[" + clz.getName() + "] fieldName[" + field.getName() + "] existence default value");
				}

				value = beanFactory.get(config.parse()).parse(beanFactory, field, config.value(), config.charset());
				field.set(obj, value);
		}
	}

	public static void setProperties(BeanFactory beanFactory, PropertiesFactory propertiesFactory, Class<?> clz, Object obj, FieldInfo field) {
		Properties properties = field.getField().getAnnotation(Properties.class);
		if (properties != null) {
			if (Modifier.isStatic(field.getField().getModifiers())) {
				Logger.warn("@Config",
						"class[" + clz.getName() + "] fieldName[" + field.getName() + "] is a static field");
			}

			Object value = null;
			try {
				if (field.forceGet(obj) != null) {
					Logger.warn("@Properties",
							"class[" + clz.getName() + "] fieldName[" + field.getName() + "] existence default value");
				}

				value = propertiesFactory.getProperties(properties.value(), field.getType());
				field.set(obj, value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void setBean(BeanFactory beanFactory, Class<?> clz, Object obj, FieldInfo field) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Autowrite s = field.getField().getAnnotation(Autowrite.class);
		if (s != null) {
			if (Modifier.isStatic(field.getField().getModifiers())) {
				Logger.warn("@AutoWrite",
						"class[" + clz.getName() + "] fieldName[" + field.getName() + "] is a static field");
			}

			String name = s.value();
			if (name.equals("")) {
				name = field.getType().getName();
			}

				if (field.forceGet(obj) != null) {
					Logger.warn("@AutoWrite",
							"class[" + clz.getName() + "] fieldName[" + field.getName() + "] existence default value");
				}

				field.set(obj, beanFactory.get(name));
		}
	}

	public static void setProxy(BeanFactory beanFactory, Class<?> clz, Object obj, FieldInfo field) throws Exception {
		Proxy proxy = field.getField().getAnnotation(Proxy.class);
		if (proxy != null) {
			if (Modifier.isStatic(field.getField().getModifiers())) {
				Logger.warn("@Proxy",
						"class[" + clz.getName() + "] fieldName[" + field.getName() + "] is a static field");
			}

			Object v = beanFactory.get(proxy.value()).getProxy(beanFactory, field.getType());
				if (field.forceGet(obj) != null) {
					Logger.warn("@Proxy",
							"class[" + clz.getName() + "] fieldName[" + field.getName() + "] existence default value");
				}

				field.set(obj, v);
		}
	}
}

class InitProcess implements Runnable {
	private Invoker invoke;
	private CountDownLatch countDown;

	public InitProcess(Invoker invoke, CountDownLatch countDownLatch) {
		this.invoke = invoke;
		this.countDown = countDownLatch;
	}

	public void run() {
		try {
			invoke.invoke();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		countDown.countDown();
	}
}
