package io.basc.framework.security.limit;

import io.basc.framework.aop.MethodInterceptor;
import io.basc.framework.aop.MethodInterceptorAccept;
import io.basc.framework.beans.factory.InstanceFactory;
import io.basc.framework.context.annotation.ConditionalOnParameters;
import io.basc.framework.core.Ordered;
import io.basc.framework.core.annotation.Annotations;
import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.data.TemporaryCounter;
import io.basc.framework.security.limit.annotation.CountLimitSecurity;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LogManager;

/**
 * count limit 实现
 * 
 * @author wcnnkh
 *
 */
@ConditionalOnParameters(order = Ordered.HIGHEST_PRECEDENCE)
public final class CountLimitMethodInterceptor implements MethodInterceptor, MethodInterceptorAccept {
	private static Logger logger = LogManager.getLogger(CountLimitMethodInterceptor.class);
	private final InstanceFactory instanceFactory;

	public CountLimitMethodInterceptor(InstanceFactory instanceFactory) {
		this.instanceFactory = instanceFactory;
	}

	public boolean isAccept(MethodInvoker invoker, Object[] args) {
		return getCountLimitSecurity(invoker) != null;
	}

	private CountLimitSecurity getCountLimitSecurity(MethodInvoker invoker) {
		CountLimitSecurity countLimitSecurity = Annotations.getAnnotation(CountLimitSecurity.class,
				invoker.getSourceClass(), invoker.getMethod());
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
		long count = temporaryCounter.incr(key, 1, 1, countLimitSecurity.period(), countLimitSecurity.timeUnit());
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
