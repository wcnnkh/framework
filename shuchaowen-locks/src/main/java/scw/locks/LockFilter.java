package scw.locks;

import scw.aop.Context;
import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.aop.Invoker;
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
@Configuration
public final class LockFilter implements Filter {
	private LockFactory lockFactory;

	public LockFilter(LockFactory lockFactory) {
		this(lockFactory, "");
	}

	public LockFilter(LockFactory lockFactory, String keyPrefix) {
		this.lockFactory = lockFactory;
	}

	public Object doFilter(Invoker invoker, Context context,
			FilterChain filterChain) throws Throwable {
		LockConfig lockConfig = AnnotationUtils.getAnnotation(LockConfig.class, context.getMethod(), context.getTargetClass());
		if (lockConfig == null) {
			return filterChain.doFilter(invoker, context);
		}

		StringBuilder sb = new StringBuilder(128);
		sb.append(context.getMethod().toString());
		ParameterDescriptor[] configs = ParameterUtils.getParameterDescriptors(context.getMethod());
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
				sb.append(JSONUtils.toJSONString(context.getArgs()[i]));
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

			return filterChain.doFilter(invoker, context);
		} finally {
			lock.unlock();
		}
	}
}
