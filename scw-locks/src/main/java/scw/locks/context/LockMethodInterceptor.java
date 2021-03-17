package scw.locks.context;

import scw.aop.MethodInterceptor;
import scw.aop.MethodInterceptorAccept;
import scw.context.annotation.Provider;
import scw.core.annotation.AnnotationUtils;
import scw.core.parameter.ParameterDescriptor;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.MethodInvoker;
import scw.json.JSONUtils;
import scw.locks.HasBeenLockedException;
import scw.locks.JdkLockFactory;
import scw.locks.Lock;
import scw.locks.LockFactory;
import scw.locks.context.annotation.LockConfig;
import scw.locks.context.annotation.LockParameter;

/**
 * 实现方法级别的分布式锁
 * 
 * @author shuchaowen
 *
 */
@Provider
public final class LockMethodInterceptor implements MethodInterceptor, MethodInterceptorAccept {
	private LockFactory lockFactory;
	
	public LockMethodInterceptor(){
		this(new JdkLockFactory());
	}

	public LockMethodInterceptor(LockFactory lockFactory) {
		this(lockFactory, "");
	}

	public LockMethodInterceptor(LockFactory lockFactory, String keyPrefix) {
		this.lockFactory = lockFactory;
	}
	
	public boolean isAccept(MethodInvoker invoker, Object[] args) {
		return AnnotationUtils.getAnnotation(LockConfig.class, invoker.getMethod(), invoker.getDeclaringClass()) != null;
	}

	public Object intercept(MethodInvoker invoker, Object[] args) throws Throwable {
		LockConfig lockConfig = AnnotationUtils.getAnnotation(LockConfig.class, invoker.getMethod(), invoker.getDeclaringClass());
		if (lockConfig == null) {
			return invoker.invoke(args);
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
			return invoker.invoke(args);
		} finally {
			lock.unlock();
		}
	}
}
