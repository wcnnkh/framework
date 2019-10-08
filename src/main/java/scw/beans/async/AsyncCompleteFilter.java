package scw.beans.async;

import java.lang.reflect.Method;

import scw.beans.annotation.AsyncComplete;
import scw.core.aop.Filter;
import scw.core.aop.FilterChain;
import scw.core.aop.Invoker;
import scw.core.aop.ProxyUtils;
import scw.core.instance.InstanceFactory;
import scw.core.utils.ClassUtils;
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
	public AsyncCompleteFilter(InstanceFactory instanceFactory){
		this.instanceFactory = instanceFactory;
	}

	private Object realFilter(Invoker invoker, Object proxy, Method method, Object[] args, FilterChain filterChain)
			throws Throwable {
		if (!isEnable()) {
			return filterChain.doFilter(invoker, proxy, method, args);
		}

		AsyncComplete asyncComplete = method.getAnnotation(AsyncComplete.class);
		if (asyncComplete == null) {
			return filterChain.doFilter(invoker, proxy, method, args);
		}

		String beanName = asyncComplete.beanName();
		if (StringUtils.isEmpty(beanName)) {
			if (ProxyUtils.isJDKProxy(proxy)) {
				beanName = method.getDeclaringClass().getName();
			} else {
				beanName = ClassUtils.getUserClass(proxy).getName();
			}
		}
		
		if(!instanceFactory.isInstance(beanName)){
			logger.warn("@AsyncComplete invalid:{}", method.getName());
			return filterChain.doFilter(invoker, proxy, method, args);
		}

		AsyncInvokeInfo info = new AsyncInvokeInfo(asyncComplete, method.getDeclaringClass(), beanName, method, args);
		AsyncCompleteService service = instanceFactory.getInstance(asyncComplete.service());
		return service.service(info);
	}

	public Object filter(Invoker invoker, Object proxy, Method method, Object[] args, FilterChain filterChain)
			throws Throwable {
		try {
			return realFilter(invoker, proxy, method, args, filterChain);
		} finally {
			ENABLE_TAG.remove();
		}
	}
}