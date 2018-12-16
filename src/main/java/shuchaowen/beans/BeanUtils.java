package shuchaowen.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;

import shuchaowen.beans.annotaion.Autowrite;
import shuchaowen.beans.annotaion.Config;
import shuchaowen.beans.annotaion.Destroy;
import shuchaowen.beans.annotaion.InitMethod;
import shuchaowen.beans.annotaion.Properties;
import shuchaowen.beans.annotaion.Service;
import shuchaowen.beans.annotaion.Transaction;
import shuchaowen.beans.property.PropertiesFactory;
import shuchaowen.beans.xml.XmlBeanParameter;
import shuchaowen.common.ClassInfo;
import shuchaowen.common.FieldInfo;
import shuchaowen.common.Logger;
import shuchaowen.common.exception.ShuChaoWenRuntimeException;
import shuchaowen.common.reflect.Invoker;
import shuchaowen.common.reflect.ReflectInvoker;
import shuchaowen.common.utils.ClassUtils;
import shuchaowen.common.utils.StringUtils;
import shuchaowen.db.DB;
import shuchaowen.web.servlet.action.annotation.Controller;

public final class BeanUtils {
	private BeanUtils() {
	};

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

	public static void initStatic(BeanFactory beanFactory, PropertiesFactory propertiesFactory, Collection<Class<?>> classList) throws Exception {
		initAutowriteStatic(beanFactory, propertiesFactory, classList);
		invokerInitStaticMethod(classList);
		initDB(beanFactory, classList);
	}
	
	public static void autoWrite(Class<?> clz, BeanFactory beanFactory, PropertiesFactory propertiesFactory, Object obj, FieldInfo field){
		setBean(beanFactory, clz, obj, field);
		setConfig(beanFactory, clz, obj, field);
		setProperties(beanFactory, propertiesFactory, clz, obj, field);
	}

	/**
	 * 过滤非静态方法和非静态字段
	 * 
	 * @param classList
	 */
	private static void initAutowriteStatic(BeanFactory beanFactory, PropertiesFactory propertiesFactory, Collection<Class<?>> classList) throws Exception {
		for (Class<?> clz : classList) {
			ClassInfo classInfo = ClassUtils.getClassInfo(clz);
			for (Entry<String, FieldInfo> entry : classInfo.getFieldMap().entrySet()) {
				FieldInfo field = entry.getValue();
				if (!Modifier.isStatic(field.getField().getModifiers())) {
					continue;
				}
				
				autoWrite(clz, beanFactory, propertiesFactory, null, field);
			}
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
	public static XmlBeanParameter[] sortParameters(Executable executable, List<XmlBeanParameter> beanMethodParameters) {
		if (executable.getParameterCount() != beanMethodParameters.size()) {
			return null;
		}

		String[] paramNames;
		if (executable instanceof Constructor) {
			paramNames = ClassUtils.getParameterName((Constructor<?>) executable);
		} else {
			paramNames = ClassUtils.getParameterName((Method) executable);
		}

		XmlBeanParameter[] methodParameters = new XmlBeanParameter[beanMethodParameters.size()];
		Class<?>[] oldTypes = executable.getParameterTypes();
		Class<?>[] types = new Class<?>[beanMethodParameters.size()];
		for (int i = 0; i < beanMethodParameters.size(); i++) {
			XmlBeanParameter beanMethodParameter = beanMethodParameters.get(i).clone();
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

	public static Object[] getBeanMethodParameterArgs(XmlBeanParameter[] beanParameters, BeanFactory beanFactory,
			shuchaowen.beans.property.PropertiesFactory propertiesFactory) throws Exception {
		Object[] args = new Object[beanParameters.length];
		for (int i = 0; i < args.length; i++) {
			XmlBeanParameter xmlBeanParameter = beanParameters[i];
			args[i] = xmlBeanParameter.parseValue(beanFactory, propertiesFactory);
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

	private static void setConfig(BeanFactory beanFactory, Class<?> clz, Object obj, FieldInfo field) {
		Config config = field.getField().getAnnotation(Config.class);
		if (config != null) {
			staticFieldWarnLog(Config.class.getName(), clz, field.getField());
			Object value = null;
			try {
				existDefaultValueWarnLog(Config.class.getName(), clz, field, obj);
				
				value = beanFactory.get(config.parse()).parse(beanFactory, field, config.value(), config.charset());
				field.set(obj, value);
			} catch (Exception e) {
				Logger.error(Config.class.getName(), "clz=" + clz.getName() + ",fieldName=" + field.getName(), e);
			}
		}
	}
	
	private static boolean checkExistDefaultValue(FieldInfo field, Object obj) throws IllegalArgumentException, IllegalAccessException{
		if(ClassUtils.containsBasicValueType(field.getType())){//值类型一定是默认值 的,所以不用判断直接所回false
			return false;
		}
		return field.forceGet(obj) != null;
	}
	
	private static void existDefaultValueWarnLog(String tag, Class<?> clz, FieldInfo field, Object obj) throws IllegalArgumentException, IllegalAccessException{
		if (checkExistDefaultValue(field, obj)) {
			Logger.warn(tag,
					"class[" + clz.getName() + "] fieldName[" + field.getName() + "] existence default value");
		}
	}
	
	private static void staticFieldWarnLog(String tag, Class<?> clz, Field field){
		if (Modifier.isStatic(field.getModifiers())) {
			Logger.warn(tag,
					"class[" + clz.getName() + "] fieldName[" + field.getName() + "] is a static field");
		}
	}

	private static void setProperties(BeanFactory beanFactory, PropertiesFactory propertiesFactory, Class<?> clz,
			Object obj, FieldInfo field) {
		Properties properties = field.getField().getAnnotation(Properties.class);
		if (properties != null) {
			staticFieldWarnLog(Properties.class.getName(), clz, field.getField());
			

			Object value = null;
			try {
				existDefaultValueWarnLog(Properties.class.getName(), clz, field, obj);
				
				String v = propertiesFactory.getValue(properties.value());
				value = StringUtils.conversion(v, field.getType());
				field.set(obj, value);
			} catch (Exception e) {
				Logger.error(Properties.class.getName(), "clz=" + clz.getName() + ",fieldName=" + field.getName(), e);
			}
		}
	}
	
	private static void setBean(BeanFactory beanFactory, Class<?> clz, Object obj, FieldInfo field) {
		Autowrite s = field.getField().getAnnotation(Autowrite.class);
		if (s != null) {
			staticFieldWarnLog(Autowrite.class.getName(), clz, field.getField());
			
			String name = s.value();
			if (name.equals("")) {
				name = field.getType().getName();
			}
			
			try {
				existDefaultValueWarnLog(Autowrite.class.getName(), clz, field, obj);
				field.set(obj, beanFactory.get(name));
			} catch (Exception e) {
				Logger.error(Autowrite.class.getName(), "clz=" + clz.getName() + ",fieldName=" + field.getName(), e);
			}
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
