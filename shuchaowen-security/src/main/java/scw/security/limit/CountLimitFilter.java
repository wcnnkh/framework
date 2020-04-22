package scw.security.limit;

import scw.aop.Context;
import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.aop.Invoker;
import scw.core.annotation.AnnotationUtils;
import scw.core.instance.InstanceFactory;
import scw.core.instance.annotation.Configuration;
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

	public Object doFilter(Invoker invoker, Context context, FilterChain filterChain) throws Throwable {
		CountLimitSecurity countLimitSecurity = AnnotationUtils.getAnnotation(CountLimitSecurity.class,
				context.getMethod(), context.getTargetClass());
		if (countLimitSecurity == null) {
			return filterChain.doFilter(invoker, context);
		}

		if (!instanceFactory.isInstance(countLimitSecurity.value())) {
			logger.warn("初始化失败：" + countLimitSecurity.value());
			throw new CountLimitException("系统错误");
		}

		CountLimitConfigFactory countLimitConfigFactory = instanceFactory.getInstance(countLimitSecurity.value());
		CountLimitConfig config = countLimitConfigFactory.getCountLimitConfig(context.getTargetClass(),
				context.getMethod(), context.getArgs());
		if (config == null) {
			return filterChain.doFilter(invoker, context);
		}

		if (!instanceFactory.isInstance(countLimitSecurity.factory())) {
			logger.warn("初始化失败：" + countLimitSecurity.factory());
			throw new CountLimitException("系统错误");
		}

		CountLimitFactory countLimitFactory = instanceFactory.getInstance(countLimitSecurity.factory());
		long count = countLimitFactory.incrAndGet(config.getName(), config.getTimeout(), config.getTimeUnit());
		boolean b = count <= config.getMaxCount();
		if (logger.isDebugEnabled()) {
			logger.debug("count limit key={}, method={}, max={}, count={}", config.getName(), context.getMethod(),
					config.getMaxCount(), count);
		}

		if (b) {
			return filterChain.doFilter(invoker, context);
		}
		logger.warn("Too frequent operation max={}, count={}, method={}", config.getMaxCount(), count,
				context.getMethod());
		throw new CountLimitException("操作过于频繁");
	}

}
