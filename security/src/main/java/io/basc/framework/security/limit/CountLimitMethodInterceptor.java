package io.basc.framework.security.limit;

import io.basc.framework.aop.MethodInterceptor;
import io.basc.framework.aop.MethodInterceptorAccept;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.core.annotation.AnnotationUtils;
import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.data.TemporaryCounter;
import io.basc.framework.factory.InstanceFactory;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.security.limit.annotation.CountLimitSecurity;

/**
 * count limit 实现
 * 
 * @author shuchaowen
 *
 */
@Provider(order = Ordered.HIGHEST_PRECEDENCE)
public final class CountLimitMethodInterceptor implements MethodInterceptor, MethodInterceptorAccept {
	private static Logger logger = LoggerFactory.getLogger(CountLimitMethodInterceptor.class);
	private final InstanceFactory instanceFactory;

	public CountLimitMethodInterceptor(InstanceFactory instanceFactory) {
		this.instanceFactory = instanceFactory;
	}

	public boolean isAccept(MethodInvoker invoker, Object[] args) {
		return getCountLimitSecurity(invoker) != null;
	}

	private CountLimitSecurity getCountLimitSecurity(MethodInvoker invoker) {
		CountLimitSecurity countLimitSecurity = AnnotationUtils.getAnnotation(CountLimitSecurity.class,
				invoker.getDeclaringClass(), invoker.getMethod());
		if (countLimitSecurity == null) {
			return null;
		}

		return countLimitSecurity.enable() ? countLimitSecurity : null;
	}

	public Object intercept(MethodInvoker invoker, Object[] args) throws Throwable {
		CountLimitSecurity countLimitSecurity = getCountLimitSecurity(invoker);
		if (countLimitSecurity == null) {
			return invoker.invoke(args);
		}

		TemporaryCounter temporaryCounter = instanceFactory.getInstance(countLimitSecurity.counter());
		CountLimitFactory countLimitFactory = instanceFactory.getInstance(countLimitSecurity.factory());
		String key = countLimitFactory.getKey(countLimitSecurity, invoker, args);
		int exp = (int) countLimitSecurity.timeUnit().toSeconds(countLimitSecurity.period());
		long count = temporaryCounter.incr(key, 1, 1, exp);
		if (logger.isDebugEnabled()) {
			logger.debug("count limit key={}, method={}, max={}, count={}", key, invoker.getMethod(),
					countLimitSecurity.maxCount(), count);
		}

		if (count > countLimitSecurity.maxCount()) {
			logger.warn("Too frequent operation max={}, count={}, method={}", key, count, invoker.getMethod());
			throw new CountLimitException("操作过于频繁");
		}
		return invoker.invoke(args);
	}

}
