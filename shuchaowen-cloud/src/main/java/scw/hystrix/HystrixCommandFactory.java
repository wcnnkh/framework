package scw.hystrix;

import scw.aop.ProxyInvoker;
import scw.beans.annotation.AutoImpl;
import scw.beans.annotation.Bean;

import com.netflix.hystrix.HystrixCommand;

@AutoImpl(DefaultHystrixCommandFactory.class)
@Bean(proxy=false)
public interface HystrixCommandFactory {
	HystrixCommand<?> getHystrixCommandFactory(ProxyInvoker invoker, Object[] args)
			throws Exception;
}
