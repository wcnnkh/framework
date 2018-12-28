package scw.beans;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import scw.beans.annotaion.Retry;
import scw.beans.annotaion.SelectCache;
import scw.common.ClassInfo;
import scw.common.Logger;
import scw.common.exception.BeansException;
import scw.common.utils.ClassUtils;
import scw.database.TransactionContext;

public final class BeanMethodInterceptor implements MethodInterceptor {
	private String[] filterNames;
	private BeanFactory beanFactory;

	public BeanMethodInterceptor(BeanFactory beanFactory, String[] filterNames) {
		this.filterNames = filterNames;
	}

	private Object run(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		boolean isTransaction = BeanUtils.isTransaction(obj.getClass(), method);
		if (isTransaction) {
			TransactionContext.getInstance().begin();
			try {
				return selectCache(obj, method, args, proxy);
			} finally {
				TransactionContext.getInstance().end();
			}
		} else {
			return selectCache(obj, method, args, proxy);
		}
	}

	private Object selectCache(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		SelectCache selectCache = obj.getClass().getAnnotation(SelectCache.class);
		if (selectCache == null) {
			return filter(obj, method, args, proxy);
		} else {
			boolean isSelectCache = BeanUtils.isSelectCache(obj.getClass(), method);
			boolean oldIsSelectCache = TransactionContext.getInstance().isSelectCache();
			if (isSelectCache == oldIsSelectCache) {
				return filter(obj, method, args, proxy);
			} else {
				TransactionContext.getInstance().setSelectCache(isSelectCache);
				try {
					return filter(obj, method, args, proxy);
				} finally {
					TransactionContext.getInstance().setSelectCache(oldIsSelectCache);
				}
			}
		}
	}

	private Object filter(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		if (filterNames == null || filterNames.length == 0) {
			return proxy.invokeSuper(obj, args);
		} else {
			List<BeanFilter> beanFilterList = getBeanFilters(obj.getClass(), method);
			BeanFilterChain beanFilterChain = new BeanFilterChain(beanFilterList);
			return beanFilterChain.doFilter(obj, method, args, proxy);
		}
	}

	private Object retry(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		ClassInfo classInfo = ClassUtils.getClassInfo(obj.getClass());
		Retry retry = AnnotationBean.getRetry(classInfo.getClz(), method);
		if (retry == null || retry.errors().length == 0) {
			return run(obj, method, args, proxy);
		} else {
			for (int i = 0; i < Math.max(retry.maxCount() + 1, 1); i++) {
				if (i != 0) {
					if (retry.log()) {
						try {
							StringBuilder sb = new StringBuilder();
							sb.append("class:").append(classInfo.getName()).append(",");
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
					return run(obj, method, args, proxy);
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
		return retry(obj, method, args, proxy);
	}

	private List<BeanFilter> getBeanFilters(Class<?> clz, Method method) {
		List<BeanFilter> beanFilters = new ArrayList<BeanFilter>(8);
		if (filterNames != null) {
			for (String name : filterNames) {
				beanFilters.add((BeanFilter) beanFactory.get(name));
			}
		}

		scw.beans.annotaion.BeanFilter beanFilter = clz.getAnnotation(scw.beans.annotaion.BeanFilter.class);
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
		return beanFilters;
	}
}
