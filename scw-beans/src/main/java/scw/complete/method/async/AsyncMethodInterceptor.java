package scw.complete.method.async;

import scw.aop.MethodInterceptor;
import scw.aop.MethodInterceptorAccept;
import scw.beans.BeanUtils;
import scw.beans.RuntimeBean;
import scw.context.annotation.Provider;
import scw.core.Ordered;
import scw.core.reflect.MethodInvoker;
import scw.instance.NoArgsInstanceFactory;
import scw.lang.NotSupportedException;

@Provider(order = Ordered.HIGHEST_PRECEDENCE)
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

	public Object intercept(MethodInvoker invoker, Object[] args) throws Throwable {
		Async async = invoker.getMethod().getAnnotation(Async.class);
		if (async == null) {
			return invoker.invoke(args);
		}

		if (isStartAsync()) {
			return invoker.invoke(args);
		}
		
		RuntimeBean runtimeBean = BeanUtils.getRuntimeBean(invoker.getInstance());
		if(runtimeBean == null){
			throw new NotSupportedException("not support async: " + invoker.getMethod());
		}

		if (!instanceFactory.isInstance(async.service())) {
			throw new NotSupportedException("not support async: " + invoker.getMethod());
		}

		AsyncMethodCompleteTask asyncMethodCompleteTask = new AsyncMethodCompleteTask(
				invoker.getMethod(), runtimeBean.getBeanDefinition().getId(), args);
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