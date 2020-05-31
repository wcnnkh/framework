package scw.locks;

import scw.aop.Filter;
import scw.aop.ProxyInvoker;
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
public final class LockFilter implements Filter {
	private LockFactory lockFactory;

	public LockFilter(LockFactory lockFactory) {
		this(lockFactory, "");
	}

	public LockFilter(LockFactory lockFactory, String keyPrefix) {
		this.lockFactory = lockFactory;
	}

	public Object doFilter(ProxyInvoker invoker, Object[] args) throws Throwable {
		LockConfig lockConfig = AnnotationUtils.getAnnotation(LockConfig.class, invoker.getMethod(), invoker.getTargetClass());
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
