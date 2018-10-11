package shuchaowen.core.beans;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import shuchaowen.core.beans.annotaion.Destroy;
import shuchaowen.core.beans.annotaion.InitMethod;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.invoke.Invoker;
import shuchaowen.core.invoke.ReflectInvoker;

public class BeanUtils {
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
		BeanInfo.initDB(beanFactory, classList);
		BeanUtils.invokerInitStaticMethod(classList);
	}

	/**
	 * 过滤非静态方法和非静态字段
	 * 
	 * @param classList
	 */
	private static void initAutowriteStatic(BeanFactory beanFactory, Collection<Class<?>> classList) throws Exception {
		for (Class<?> clz : classList) {
			BeanInfo.autoWriteStatic(clz, beanFactory);
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
