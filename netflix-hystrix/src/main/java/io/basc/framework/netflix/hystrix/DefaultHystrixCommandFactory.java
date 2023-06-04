package io.basc.framework.netflix.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommand.Setter;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;

import io.basc.framework.beans.factory.InstanceFactory;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.netflix.hystrix.annotation.Hystrix;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class DefaultHystrixCommandFactory implements HystrixCommandFactory {
	private InstanceFactory instanceFactory;

	public DefaultHystrixCommandFactory(InstanceFactory instanceFactory) {
		this.instanceFactory = instanceFactory;
	}

	public HystrixCommand<?> getHystrixCommandFactory(MethodInvoker invoker, Object[] args) throws Exception {
		Hystrix hystrix = invoker.getSourceClass().getAnnotation(Hystrix.class);
		if (hystrix == null) {
			return null;
		}

		Setter setter = Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(invoker.getSourceClass().getName()))
				.andCommandKey(HystrixCommandKey.Factory.asKey(invoker.getMethod().toString()));
		afterSetter(setter);
		Object fallback = invoker.getSourceClass().isAssignableFrom(hystrix.fallback())
				? instanceFactory.getInstance(hystrix.fallback())
				: null;
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
