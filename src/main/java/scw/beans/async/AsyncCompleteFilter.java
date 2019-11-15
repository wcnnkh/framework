package scw.beans.async;

import java.lang.reflect.Method;

import scw.beans.annotation.AsyncComplete;
import scw.core.aop.Filter;
import scw.core.aop.FilterChain;
import scw.core.aop.Invoker;
import scw.core.instance.InstanceFactory;
import scw.core.utils.StringUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

/**
 * 只能受BeanFactory管理
 * 
 * @author shuchaowen
 *
 */
public final class AsyncCompleteFilter implements Filter {
	private static ThreadLocal<Boolean> ENABLE_TAG = new ThreadLocal<Boolean>();
	private static Logger logger = LoggerUtils.getLogger(AsyncCompleteFilter.class);

	public static boolean isEnable() {
		Boolean b = ENABLE_TAG.get();
		return b == null ? true : b;
	}

	public static void setEnable(boolean enable) {
		ENABLE_TAG.set(enable);
	}

	private InstanceFactory instanceFactory;

	public AsyncCompleteFilter(InstanceFactory instanceFactory) {
		this.instanceFactory = instanceFactory;
	}

	private Object realFilter(Invoker invoker, Object proxy, Class<?> targetClass, Method method, Object[] args, FilterChain filterChain)
			throws Throwable {
		if (!isEnable()) {
			return filterChain.doFilter(invoker, proxy, targetClass, method, args);
		}

		AsyncComplete asyncComplete = method.getAnnotation(AsyncComplete.class);
		if (asyncComplete == null) {
			return filterChain.doFilter(invoker, proxy, targetClass, method, args);
		}

		String beanName = asyncComplete.beanName();
		if (StringUtils.isEmpty(beanName)) {
			beanName = targetClass.getName();
		}

		if (!instanceFactory.isSingleton(beanName) || !instanceFactory.isInstance(beanName)) {
			logger.warn("[{}]不支持使用@AsyncComplete注解:{}", beanName, method);
			return filterChain.doFilter(invoker, proxy, targetClass, method, args);
		}

		AsyncInvokeInfo info = new AsyncInvokeInfo(asyncComplete, method.getDeclaringClass(), beanName, method, args);
		AsyncCompleteService service = instanceFactory.getInstance(asyncComplete.service());
		return service.service(info);
	}

	public Object doFilter(Invoker invoker, Object proxy, Class<?> targetClass, Method method, Object[] args, FilterChain filterChain)
			throws Throwable {
		try {
			return realFilter(invoker, proxy, targetClass, method, args, filterChain);
		} finally {
			ENABLE_TAG.remove();
		}
	}
}