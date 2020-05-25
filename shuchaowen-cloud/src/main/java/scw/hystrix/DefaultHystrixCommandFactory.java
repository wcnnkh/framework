package scw.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommand.Setter;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;

import scw.aop.FilterChain;
import scw.aop.Invoker;
import scw.aop.ProxyContext;
import scw.core.instance.NoArgsInstanceFactory;
import scw.hystrix.annotation.Hystrix;

public class DefaultHystrixCommandFactory implements HystrixCommandFactory {
	private NoArgsInstanceFactory instanceFactory;

	public DefaultHystrixCommandFactory(NoArgsInstanceFactory instanceFactory) {
		this.instanceFactory = instanceFactory;
	}

	@Override
	public HystrixCommand<?> getHystrixCommandFactory(ProxyContext context, Invoker invoker, FilterChain filterChain)
			throws Exception {
		Hystrix hystrix = context.getTargetClass().getAnnotation(Hystrix.class);
		if (hystrix == null) {
			return null;
		}

		Setter setter = Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(context.getTargetClass().getName()))
				.andCommandKey(HystrixCommandKey.Factory.asKey(context.getMethod().toString()));
		afterSetter(setter);
		Object fallback = context.getTargetClass().isAssignableFrom(hystrix.fallback())
				? instanceFactory.getInstance(hystrix.fallback()) : null;
		return new HystrixFilterCommand(setter, fallback, context, invoker, filterChain);
	}

	protected void afterSetter(Setter setter) {
		setter.andCommandPropertiesDefaults(
				HystrixCommandProperties.Setter().withCircuitBreakerRequestVolumeThreshold(10)// 至少有10个请求，熔断器才进行错误率的计算
						.withCircuitBreakerSleepWindowInMilliseconds(5000)// 熔断器中断请求5秒后会进入半打开状态,放部分流量过去重试
						.withCircuitBreakerErrorThresholdPercentage(50)//// 错误率达到50开启熔断保护
						.withExecutionTimeoutEnabled(true))
				.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(10));// 使用线程池，并设置核心线程数为10
	}
}
