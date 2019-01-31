package scw.beans;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashSet;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import scw.beans.annotaion.Retry;
import scw.common.Logger;
import scw.common.exception.BeansException;

/**
 * 顶层的filter
 * 
 * @author shuchaowen
 *
 */
public final class BeanMethodInterceptor implements MethodInterceptor {
	private String[] filterNames;
	private BeanFactory beanFactory;

	public BeanMethodInterceptor(BeanFactory beanFactory, String[] filterNames) {
		this.filterNames = filterNames;
		this.beanFactory = beanFactory;
	}

	private Object filter(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		// 把重复的filter过渡
		LinkedHashSet<String> filterSet = new LinkedHashSet<String>();
		if (filterNames != null) {
			for (String name : filterNames) {
				filterSet.add(name);
			}
		}

		scw.beans.annotaion.BeanFilter beanFilter = method.getDeclaringClass()
				.getAnnotation(scw.beans.annotaion.BeanFilter.class);
		if (beanFilter != null) {
			for (Class<? extends BeanFilter> c : beanFilter.value()) {
				filterSet.add(c.getName());
			}
		}

		beanFilter = method.getAnnotation(scw.beans.annotaion.BeanFilter.class);
		if (beanFilter != null) {
			for (Class<? extends BeanFilter> c : beanFilter.value()) {
				filterSet.add(c.getName());
			}
		}

		BeanFilterChain beanFilterChain = new BeanFilterChain(beanFactory, filterSet);
		return beanFilterChain.doFilter(obj, method, args, proxy);
	}

	private Object retry(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		Retry retry = BeanUtils.getRetry(method);
		if (retry == null || retry.errors().length == 0) {
			return filter(obj, method, args, proxy);
		} else {
			for (int i = 0; i < Math.max(retry.maxCount() + 1, 1); i++) {
				if (i != 0) {
					if (retry.log()) {
						try {
							StringBuilder sb = new StringBuilder();
							sb.append("class:").append(method.getDeclaringClass().getName()).append(",");
							sb.append("method:").append(method.getName()).append(",");
							sb.append("parameterTypes:").append(Arrays.toString(method.getParameterTypes()))
									.append(",");
							sb.append("args:").append(Arrays.toString(args));
							Logger.info("@Retry", sb.toString());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					if (retry.delayMillis() > 0) {
						retry.timeUnit().sleep(retry.delayMillis());
					}
				}

				try {
					return filter(obj, method, args, proxy);
				} catch (Throwable e) {
					boolean find = false;
					for (Class<? extends Throwable> error : retry.errors()) {
						if (e.getClass().isAssignableFrom(error)) {
							find = true;
							break;
						}
					}

					if (find) {// 找到对应异常
						e.printStackTrace();
					} else {
						throw e;// 找不到就抛出异常，不再重试
					}
				}
			}
		}
		throw new BeansException();
	}

	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		return transaction(obj, method, args, proxy);
	}

	private Object transaction(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		/*
		 * TransactionManager.before(); Transactional clzTransaction =
		 * method.getDeclaringClass().getAnnotation(Transactional.class);
		 * Transactional methodTransaction =
		 * method.getAnnotation(Transactional.class); if (clzTransaction != null
		 * || methodTransaction != null) { boolean b = true; if (clzTransaction
		 * != null) { b = clzTransaction.value(); }
		 * 
		 * if (methodTransaction != null) { b = clzTransaction.value(); }
		 * TransactionManager.getResource().getTransactionConfig().setActive(!b)
		 * ; }
		 * 
		 * try { Object rtn = invoke(obj, method, args, proxy);
		 * TransactionManager.after(); return rtn; } finally {
		 * TransactionManager.complete(); }
		 */
		return invoke(obj, method, args, proxy);
	}

	private Object invoke(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		if (obj instanceof BeanFilter) {
			return proxy.invokeSuper(obj, args);
		}
		return retry(obj, method, args, proxy);
	}
}
