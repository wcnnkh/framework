package scw.security.limit;

import scw.aop.MethodInterceptor;
import scw.aop.MethodInterceptorAccept;
import scw.aop.MethodInterceptorChain;
import scw.context.annotation.Provider;
import scw.core.annotation.AnnotationUtils;
import scw.core.reflect.MethodInvoker;
import scw.data.TemporaryCounter;
import scw.instance.factory.InstanceFactory;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.security.limit.annotation.CountLimitSecurity;

/**
 * count limit 实现
 * 
 * @author shuchaowen
 *
 */
@Provider(order = Integer.MAX_VALUE)
public final class CountLimitMethodInterceptor implements MethodInterceptor, MethodInterceptorAccept {
	private static Logger logger = LoggerUtils.getLogger(CountLimitMethodInterceptor.class);
	private final InstanceFactory instanceFactory;

	public CountLimitMethodInterceptor(InstanceFactory instanceFactory) {
		this.instanceFactory = instanceFactory;
	}

	public boolean isAccept(MethodInvoker invoker, Object[] args) {
		return getCountLimitSecurity(invoker) != null;
	}

	private CountLimitSecurity getCountLimitSecurity(MethodInvoker invoker) {
		CountLimitSecurity countLimitSecurity = AnnotationUtils.getAnnotation(CountLimitSecurity.class,
				invoker.getSourceClass(), invoker.getMethod());
		if (countLimitSecurity == null) {
			return null;
		}

		return countLimitSecurity.enable() ? countLimitSecurity : null;
	}

	public Object intercept(MethodInvoker invoker, Object[] args, MethodInterceptorChain filterChain) throws Throwable {
		CountLimitSecurity countLimitSecurity = getCountLimitSecurity(invoker);
		if (countLimitSecurity == null) {
			return filterChain.intercept(invoker, args);
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
		return filterChain.intercept(invoker, args);
	}

}
