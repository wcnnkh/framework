package scw.locks;

import scw.aop.MethodInterceptor;
import scw.aop.MethodInterceptorAccept;
import scw.aop.MethodInterceptorChain;
import scw.aop.MethodInvoker;
import scw.core.annotation.AnnotationUtils;
import scw.core.instance.annotation.Configuration;
import scw.core.parameter.ParameterDescriptor;
import scw.core.parameter.ParameterUtils;
import scw.json.JSONUtils;
import scw.locks.annotation.LockConfig;
import scw.locks.annotation.LockParameter;

/**
 * 实现方法级别的分布式锁
 * 
 * @author shuchaowen
 *
 */
@Configuration(order=Integer.MAX_VALUE)
public final class LockMethodInterceptor implements MethodInterceptor, MethodInterceptorAccept {
	private LockFactory lockFactory;

	public LockMethodInterceptor(LockFactory lockFactory) {
		this(lockFactory, "");
	}

	public LockMethodInterceptor(LockFactory lockFactory, String keyPrefix) {
		this.lockFactory = lockFactory;
	}
	
	public boolean isAccept(MethodInvoker invoker, Object[] args) {
		return AnnotationUtils.getAnnotation(LockConfig.class, invoker.getMethod(), invoker.getSourceClass()) != null;
	}

	public Object intercept(MethodInvoker invoker, Object[] args, MethodInterceptorChain filterChain) throws Throwable {
		LockConfig lockConfig = AnnotationUtils.getAnnotation(LockConfig.class, invoker.getMethod(), invoker.getSourceClass());
		if (lockConfig == null) {
			return filterChain.intercept(invoker, args);
		}

		StringBuilder sb = new StringBuilder(128);
		sb.append(invoker.getMethod().toString());
		ParameterDescriptor[] configs = ParameterUtils.getParameterDescriptors(invoker.getMethod());
		for (int i = 0; i < configs.length; i++) {
			ParameterDescriptor config = configs[i];
			boolean b = lockConfig.all();
			LockParameter lockParameter = config.getAnnotatedElement().getAnnotation(LockParameter.class);
			if (lockParameter != null) {
				b = lockParameter.value();
			}

			if (b) {
				sb.append(i == 0 ? "?" : "&");
				sb.append(config.getName());
				sb.append("=");
				sb.append(JSONUtils.toJSONString(args[i]));
			}
		}

		String lockKey = sb.toString();
		Lock lock = lockFactory.getLock(lockKey);
		try {
			if (lockConfig.isWait()) {
				lock.lock();
			} else if (!lock.tryLock()) {
				throw new HasBeenLockedException(lockKey);
			}

			return filterChain.intercept(invoker, args);
		} finally {
			lock.unlock();
		}
	}
}
