package scw.complete.method.async;

import scw.aop.MethodInterceptor;
import scw.aop.MethodInterceptorAccept;
import scw.aop.MethodInterceptorChain;
import scw.aop.MethodInvoker;
import scw.core.instance.NoArgsInstanceFactory;
import scw.core.instance.annotation.Configuration;
import scw.core.utils.StringUtils;
import scw.lang.NotSupportedException;

@Configuration(order = Integer.MAX_VALUE)
public final class AsyncMethodInterceptor implements MethodInterceptor, MethodInterceptorAccept {
	private static ThreadLocal<Boolean> TAG_THREAD_LOCAL = new ThreadLocal<Boolean>();
	private final NoArgsInstanceFactory instanceFactory;

	public AsyncMethodInterceptor(NoArgsInstanceFactory instanceFactory) {
		this.instanceFactory = instanceFactory;
	}

	public static boolean isStartAsync() {
		Boolean tag = TAG_THREAD_LOCAL.get();
		return tag != null && tag;
	}

	public static void startAsync() {
		TAG_THREAD_LOCAL.set(true);
	}

	public static void endAsync() {
		TAG_THREAD_LOCAL.set(false);
	}
	
	public boolean isAccept(MethodInvoker invoker, Object[] args) {
		Async async = invoker.getMethod().getAnnotation(Async.class);
		if (async == null) {
			return false;
		}

		if (isStartAsync()) {
			return false;
		}
		
		return true;
	}

	public Object intercept(MethodInvoker invoker, Object[] args, MethodInterceptorChain filterChain) throws Throwable {
		Async async = invoker.getMethod().getAnnotation(Async.class);
		if (async == null) {
			return filterChain.intercept(invoker, args);
		}

		if (isStartAsync()) {
			return filterChain.intercept(invoker, args);
		}

		if (!instanceFactory.isInstance(async.service())) {
			throw new NotSupportedException("not support async: " + invoker.getMethod());
		}

		String beanName = StringUtils.isEmpty(async.beanName()) ? invoker.getSourceClass().getName() : async.beanName();
		if (!instanceFactory.isInstance(beanName)) {
			throw new NotSupportedException(invoker.getMethod() + " --> beanName: " + beanName);
		}

		AsyncMethodCompleteTask asyncMethodCompleteTask = new AsyncMethodCompleteTask(
				invoker.getMethod(), beanName, args);
		AsyncMethodService asyncService = instanceFactory.getInstance(async.service());
		startAsync();
		try {
			asyncService.service(asyncMethodCompleteTask);
		} finally {
			endAsync();
		}
		return null;
	}
}