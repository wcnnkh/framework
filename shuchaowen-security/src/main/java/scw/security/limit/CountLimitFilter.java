package scw.security.limit;

import java.lang.reflect.Method;

import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.aop.Invoker;
import scw.beans.annotation.Configuration;
import scw.core.annotation.AnnotationUtils;
import scw.core.instance.InstanceFactory;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.security.limit.annotation.CountLimitSecurity;

/**
 * count limit 实现
 * 
 * @author shuchaowen
 *
 */
@Configuration
public final class CountLimitFilter implements Filter {
	private static Logger logger = LoggerUtils.getLogger(CountLimitFilter.class);
	private final InstanceFactory instanceFactory;

	public CountLimitFilter(InstanceFactory instanceFactory) {
		this.instanceFactory = instanceFactory;
	}

	public Object doFilter(Invoker invoker, Object proxy, Class<?> targetClass, Method method, Object[] args,
			FilterChain filterChain) throws Throwable {
		CountLimitSecurity countLimitSecurity = AnnotationUtils.getAnnotation(CountLimitSecurity.class, targetClass,
				method);
		if (countLimitSecurity == null) {
			return filterChain.doFilter(invoker, proxy, targetClass, method, args);
		}

		if (!instanceFactory.isInstance(countLimitSecurity.value())) {
			logger.warn("初始化失败：" + countLimitSecurity.value());
			throw new CountLimitException("系统错误");
		}

		CountLimitConfigFactory countLimitConfigFactory = instanceFactory.getInstance(countLimitSecurity.value());
		CountLimitConfig config = countLimitConfigFactory.getCountLimitConfig(targetClass, method, args);
		if (config == null) {
			return filterChain.doFilter(invoker, proxy, targetClass, method, args);
		}

		if (!instanceFactory.isInstance(countLimitSecurity.factory())) {
			logger.warn("初始化失败：" + countLimitSecurity.factory());
			throw new CountLimitException("系统错误");
		}

		CountLimitFactory countLimitFactory = instanceFactory.getInstance(countLimitSecurity.factory());
		long count = countLimitFactory.incrAndGet(config.getName(), config.getTimeout(), config.getTimeUnit());
		boolean b = count <= config.getMaxCount();
		if (logger.isDebugEnabled()) {
			logger.debug("count limit key={}, method={}, max={}, count={}", config.getName(), method,
					config.getMaxCount(), count);
		}

		if (b) {
			return filterChain.doFilter(invoker, proxy, targetClass, method, args);
		}
		logger.warn("Too frequent operation max={}, count={}, method={}", config.getMaxCount(), count, method);
		throw new CountLimitException("操作过于频繁");
	}

}
