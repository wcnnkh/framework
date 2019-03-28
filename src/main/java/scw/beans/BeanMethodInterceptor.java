package scw.beans;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import scw.aop.DefaultFilterChain;
import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.aop.Invoker;
import scw.aop.cglib.CglibInvoker;
import scw.beans.annotaion.BeanFilter;
import scw.beans.annotaion.Retry;
import scw.common.exception.BeansException;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

/**
 * 顶层的filter
 * 
 * @author shuchaowen
 *
 */
public final class BeanMethodInterceptor implements MethodInterceptor {
	private static Logger logger = LoggerFactory.getLogger(BeanMethodInterceptor.class);

	private String[] filterNames;
	private BeanFactory beanFactory;
	private volatile Collection<Filter> filters;

	public BeanMethodInterceptor(BeanFactory beanFactory, String[] filterNames) {
		this.filterNames = filterNames;
		this.beanFactory = beanFactory;
	}

	private void initFilters(Class<?> clz, Method method) {
		if (filters == null) {
			HashSet<Filter> filters = new HashSet<Filter>();
			if (filterNames != null) {
				for (String name : filterNames) {
					Filter filter = beanFactory.get(name);
					filters.add(filter);
				}
			}

			scw.beans.annotaion.BeanFilter beanFilter = method.getDeclaringClass()
					.getAnnotation(scw.beans.annotaion.BeanFilter.class);
			if (beanFilter != null) {
				for (Class<? extends Filter> c : beanFilter.value()) {
					filters.add(beanFactory.get(c));
				}
			}

			beanFilter = method.getAnnotation(scw.beans.annotaion.BeanFilter.class);
			if (beanFilter != null) {
				for (Class<? extends Filter> c : beanFilter.value()) {
					filters.add(beanFactory.get(c));
				}
			}

			if (this.filters == null) {
				synchronized (this) {
					if (this.filters == null) {
						this.filters = new ArrayList<Filter>(filters);
					}
				}
			}
		}
	}

	private Object filter(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		// 把重复的filter过渡
		initFilters(method.getDeclaringClass(), method);
		FilterChain filterChain = new DefaultFilterChain(filters);
		Invoker invoker = new CglibInvoker(proxy, obj);
		return filterChain.doFilter(invoker, obj, method, args);
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
							logger.debug("retry " + sb.toString());
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
		throw new BeansException(method.getDeclaringClass().getName());
	}

	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		if (obj instanceof BeanFilter) {
			return proxy.invokeSuper(obj, args);
		}
		return retry(obj, method, args, proxy);
	}

}
