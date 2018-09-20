package shuchaowen.core.beans;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import net.sf.cglib.proxy.Enhancer;
import shuchaowen.core.beans.annotaion.Autowrite;
import shuchaowen.core.beans.annotaion.Config;
import shuchaowen.core.beans.annotaion.Destroy;
import shuchaowen.core.beans.annotaion.InitMethod;
import shuchaowen.core.beans.annotaion.Proxy;
import shuchaowen.core.beans.annotaion.Service;
import shuchaowen.core.beans.annotaion.Transaction;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.http.server.annotation.Controller;
import shuchaowen.core.invoke.Invoker;
import shuchaowen.core.invoke.ReflectInvoker;
import shuchaowen.core.util.FieldInfo;

public class BeanUtils {
	private static final TransactionMethodInterceptor transactionMethodInterceptor = new TransactionMethodInterceptor();
	
	/**
	 * 调用init方法
	 * 
	 * @param beanFactory
	 * @param classList
	 * @throws Exception
	 */
	public static void invokerInitStaticMethod(Collection<Class<?>> classList) throws Exception {
		Map<Integer, List<Invoker>> map = new HashMap<Integer, List<Invoker>>();
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
				int level = initMethod.weight();
				List<Invoker> p = map.getOrDefault(level, new ArrayList<Invoker>());
				p.add(invoke);
				map.put(level, p);
			}
		}

		// 调用指定注解的方法
		List<Integer> levelList = new ArrayList<Integer>(map.keySet());
		Collections.sort(levelList);
		for (int i = levelList.size() - 1; i >= 0; i--) {
			int level = levelList.get(i);
			List<Invoker> infoList = map.get(level);
			if (infoList.size() == 0) {
				continue;
			}

			CountDownLatch countDownLatch = new CountDownLatch(infoList.size());
			for (Invoker info : infoList) {
				InitProcess process = new InitProcess(info, countDownLatch);
				new Thread(process).start();
			}
			countDownLatch.await();
		}
	}

	public static void destroyStaticMethod(Collection<Class<?>> classList) throws Exception {
		Map<Integer, List<Invoker>> map = new HashMap<Integer, List<Invoker>>();
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
				int level = destroy.weight();
				List<Invoker> p = map.getOrDefault(level, new ArrayList<Invoker>());
				p.add(invoke);
				map.put(level, p);
			}
		}

		// 调用指定注解的方法
		List<Integer> levelList = new ArrayList<Integer>(map.keySet());
		Collections.sort(levelList);
		for (int i = levelList.size() - 1; i >= 0; i--) {
			int level = levelList.get(i);
			List<Invoker> infoList = map.get(level);
			if (infoList.size() == 0) {
				continue;
			}

			CountDownLatch countDownLatch = new CountDownLatch(infoList.size());
			for (Invoker info : infoList) {
				InitProcess process = new InitProcess(info, countDownLatch);
				new Thread(process).start();
			}
			countDownLatch.await();
		}
	}

	/**
	 * 过滤非静态方法和非静态字段
	 * 
	 * @param classList
	 */
	public static void initAutowriteStatic(BeanFactory beanFactory, Collection<Class<?>> classList) throws Exception {
		for (Class<?> clz : classList) {
			for (Field field : clz.getDeclaredFields()) {
				if (!Modifier.isStatic(field.getModifiers())) {
					continue;
				}

				FieldInfo fieldInfo = new FieldInfo(clz, field);

				Autowrite s = field.getAnnotation(Autowrite.class);
				if (s != null) {
					String name = s.name();
					if (name.equals("")) {
						name = field.getType().getName();
					}
					fieldInfo.set(null, beanFactory.get(name));
				}

				Proxy proxy = field.getAnnotation(Proxy.class);
				if (proxy != null) {
					fieldInfo.set(null, (beanFactory.get(proxy.value())).getProxy(beanFactory, field.getType()));
				}

				Config config = field.getAnnotation(Config.class);
				if (config != null) {
					setConfig(beanFactory, clz, null, field);
				}
			}
		}
	}

	public static Object wrapper(BeanFactory beanFactory, Class<?> clz, Object obj) throws Exception {
		wrapperAutowrite(beanFactory, clz, obj);
		wrapperConsumer(beanFactory, clz, obj);
		wrapperConfig(beanFactory, clz, obj);
		wrapperInitMethod(beanFactory, clz, obj);
		return obj;
	}

	public static Object wrapperConsumer(BeanFactory beanFactory, Class<?> clz, Object obj) throws Exception {
		Class<?> tempClz = clz;
		while (tempClz != null) {
			for (Field field : tempClz.getDeclaredFields()) {
				if (Modifier.isStatic(field.getModifiers())) {
					continue;
				}

				Proxy proxy = field.getAnnotation(Proxy.class);
				if (proxy == null) {
					continue;
				}

				FieldInfo fieldInfo = new FieldInfo(tempClz, field);
				fieldInfo.set(obj, beanFactory.get(proxy.value()).getProxy(beanFactory, field.getType()));
			}
			tempClz = tempClz.getSuperclass();
		}
		return obj;
	}

	public static Object wrapperDestoryMethod(BeanFactory beanFactory, Class<?> clz, Object obj) throws Exception {
		Map<Integer, List<Invoker>> map = new HashMap<Integer, List<Invoker>>();
		Class<?> tempClz = clz;
		for (Method method : tempClz.getDeclaredMethods()) {
			if (Modifier.isStatic(method.getModifiers())) {
				continue;
			}

			Destroy destroy = method.getAnnotation(Destroy.class);
			if (destroy == null) {
				continue;
			}

			if (method.getParameterCount() != 0) {
				throw new ShuChaoWenRuntimeException("ClassName=" + clz.getName() + ",MethodName=" + method.getName()
						+ "There must be no parameter.");
			}

			Invoker invoke = new ReflectInvoker(beanFactory, tempClz, method);
			int level = destroy.weight();
			List<Invoker> p = map.getOrDefault(level, new ArrayList<Invoker>());
			p.add(invoke);
			map.put(level, p);
		}
		tempClz = tempClz.getSuperclass();

		// 调用指定注解的方法
		List<Integer> levelList = new ArrayList<Integer>(map.keySet());
		Collections.sort(levelList);
		for (int i = levelList.size() - 1; i >= 0; i--) {
			int level = levelList.get(i);
			List<Invoker> infoList = map.get(level);
			if (infoList.size() == 0) {
				continue;
			}

			CountDownLatch countDownLatch = new CountDownLatch(infoList.size());
			for (Invoker info : infoList) {
				InitProcess process = new InitProcess(info, countDownLatch);
				new Thread(process).start();
			}
			countDownLatch.await();
		}
		return obj;
	}

	public static Object wrapperInitMethod(BeanFactory beanFactory, Class<?> clz, Object obj) throws Exception {
		Map<Integer, List<Invoker>> map = new HashMap<Integer, List<Invoker>>();
		Class<?> tempClz = clz;
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
					throw new ShuChaoWenRuntimeException("ClassName=" + clz.getName() + ",MethodName="
							+ method.getName() + "There must be no parameter.");
				}

				Invoker invoke = new ReflectInvoker(beanFactory, tempClz, method);
				int level = initMethod.weight();
				List<Invoker> p = map.getOrDefault(level, new ArrayList<Invoker>());
				p.add(invoke);
				map.put(level, p);
			}
			tempClz = tempClz.getSuperclass();
		}

		// 调用指定注解的方法
		List<Integer> levelList = new ArrayList<Integer>(map.keySet());
		Collections.sort(levelList);
		for (int i = levelList.size() - 1; i >= 0; i--) {
			int level = levelList.get(i);
			List<Invoker> infoList = map.get(level);
			if (infoList.size() == 0) {
				continue;
			}

			CountDownLatch countDownLatch = new CountDownLatch(infoList.size());
			for (Invoker info : infoList) {
				InitProcess process = new InitProcess(info, countDownLatch);
				new Thread(process).start();
			}
			countDownLatch.await();
		}
		return obj;
	}

	public static Object wrapperAutowrite(BeanFactory beanFactory, Class<?> clz, Object obj) throws Exception {
		Class<?> tempClz = clz;
		while (tempClz != null) {
			for (Field field : tempClz.getDeclaredFields()) {
				if (Modifier.isStatic(field.getModifiers())) {
					continue;
				}

				Autowrite s = field.getAnnotation(Autowrite.class);
				if (s != null) {
					String name = s.name();
					if (name.equals("")) {
						name = field.getType().getName();
					}
					FieldInfo fieldInfo = new FieldInfo(tempClz, field);
					fieldInfo.set(obj, beanFactory.get(name));
				}
			}
			tempClz = tempClz.getSuperclass();
		}
		return obj;
	}

	private static void setConfig(BeanFactory beanFactory, Class<?> clz, Object obj, Field field) throws Exception {
		Config config = field.getAnnotation(Config.class);
		if (config != null) {
			FieldInfo fieldInfo = new FieldInfo(clz, field);
			Object value = beanFactory.get(config.parse()).parse(beanFactory, fieldInfo, config.value(),
					config.charset());
			fieldInfo.set(obj, value);
		}
	}

	public static Object wrapperConfig(BeanFactory beanFactory, Class<?> clz, Object obj) throws Exception {
		Class<?> tempClz = clz;
		while (tempClz != null) {
			for (Field field : tempClz.getDeclaredFields()) {
				if (Modifier.isStatic(field.getModifiers())) {
					continue;
				}

				setConfig(beanFactory, tempClz, obj, field);
			}
			tempClz = tempClz.getSuperclass();
		}
		return obj;
	}

	// 这个类是否应该使用代理
	private static boolean isProxy(Class<?> type) {
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

	public static Object getProxy(final Class<?> type) throws InstantiationException, IllegalAccessException {
		if (isProxy(type)) {
			return Enhancer.create(type, transactionMethodInterceptor);
		} else {
			return type.newInstance();
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
