package scw.hystrix;

import com.netflix.hystrix.HystrixCommand;

import scw.aop.FilterChain;
import scw.aop.Invoker;
import scw.aop.ProxyContext;
import scw.beans.annotation.AutoImpl;
import scw.beans.annotation.Bean;

@AutoImpl(DefaultHystrixCommandFactory.class)
@Bean(proxy=false)
public interface HystrixCommandFactory {
	HystrixCommand<?> getHystrixCommandFactory(ProxyContext context, Invoker invoker, FilterChain filterChain)
			throws Exception;
}
