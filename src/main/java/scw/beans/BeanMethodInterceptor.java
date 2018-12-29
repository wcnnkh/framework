package scw.beans;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import scw.beans.annotaion.Retry;
import scw.common.Logger;
import scw.common.exception.BeansException;

public final class BeanMethodInterceptor implements MethodInterceptor {
	private String[] filterNames;
	private BeanFactory beanFactory;

	public BeanMethodInterceptor(BeanFactory beanFactory, String[] filterNames) {
		this.filterNames = filterNames;
		this.beanFactory = beanFactory;
	}

	private Object filter(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		List<BeanFilter> beanFilters = new ArrayList<BeanFilter>(4);
		if (filterNames != null) {
			for (String name : filterNames) {
				beanFilters.add((BeanFilter) beanFactory.get(name));
			}
		}

		scw.beans.annotaion.BeanFilter beanFilter = method.getDeclaringClass()
				.getAnnotation(scw.beans.annotaion.BeanFilter.class);
		if (beanFilter != null) {
			if (beanFilter.namePriority()) {
				for (String name : beanFilter.name()) {
					beanFilters.add((BeanFilter) beanFactory.get(name));
				}

				for (Class<? extends BeanFilter> c : beanFilter.value()) {
					beanFilters.add(beanFactory.get(c));
				}
			} else {
				for (Class<? extends BeanFilter> c : beanFilter.value()) {
					beanFilters.add(beanFactory.get(c));
				}

				for (String name : beanFilter.name()) {
					beanFilters.add((BeanFilter) beanFactory.get(name));
				}
			}
		}

		beanFilter = method.getAnnotation(scw.beans.annotaion.BeanFilter.class);
		if (beanFilter != null) {
			if (beanFilter.namePriority()) {
				for (String name : beanFilter.name()) {
					beanFilters.add((BeanFilter) beanFactory.get(name));
				}

				for (Class<? extends BeanFilter> c : beanFilter.value()) {
					beanFilters.add(beanFactory.get(c));
				}
			} else {
				for (Class<? extends BeanFilter> c : beanFilter.value()) {
					beanFilters.add(beanFactory.get(c));
				}

				for (String name : beanFilter.name()) {
					beanFilters.add((BeanFilter) beanFactory.get(name));
				}
			}
		}

		BeanFilterChain beanFilterChain = new BeanFilterChain(beanFilters);
		return beanFilterChain.doFilter(obj, method, args, proxy);
	}

	private Object retry(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		Retry retry = BeanUtils.getRetry(method.getDeclaringClass(), method);
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

					if (retry.delayMillis() > 0 || retry.delayNanos() > 0) {
						Thread.sleep(Math.abs(retry.delayMillis()), Math.abs(retry.delayNanos()));
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
		if (obj instanceof BeanFilter) {
			return proxy.invokeSuper(obj, args);
		}

		return retry(obj, method, args, proxy);
	}
}
