package scw.netflix.hystrix;

import scw.core.reflect.MethodInvoker;
import scw.instance.NoArgsInstanceFactory;
import scw.netflix.hystrix.annotation.Hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommand.Setter;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;

public class DefaultHystrixCommandFactory implements HystrixCommandFactory {
	private NoArgsInstanceFactory instanceFactory;

	public DefaultHystrixCommandFactory(NoArgsInstanceFactory instanceFactory) {
		this.instanceFactory = instanceFactory;
	}

	public HystrixCommand<?> getHystrixCommandFactory(MethodInvoker invoker, Object[] args)
			throws Exception {
		Hystrix hystrix = invoker.getSourceClass().getAnnotation(Hystrix.class);
		if (hystrix == null) {
			return null;
		}

		Setter setter = Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(invoker.getSourceClass().getName()))
				.andCommandKey(HystrixCommandKey.Factory.asKey(invoker.getMethod().toString()));
		afterSetter(setter);
		Object fallback = invoker.getSourceClass().isAssignableFrom(hystrix.fallback())
				? instanceFactory.getInstance(hystrix.fallback()) : null;
		return new HystrixFilterCommand(setter, fallback, invoker, args);
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
