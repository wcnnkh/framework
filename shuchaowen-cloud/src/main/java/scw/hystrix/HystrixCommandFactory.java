package scw.hystrix;

import com.netflix.hystrix.HystrixCommand;

import scw.aop.ProxyInvoker;
import scw.beans.annotation.AopEnable;
import scw.beans.annotation.AutoImpl;

@AutoImpl(DefaultHystrixCommandFactory.class)
@AopEnable(false)
public interface HystrixCommandFactory {
	HystrixCommand<?> getHystrixCommandFactory(ProxyInvoker invoker, Object[] args)
			throws Exception;
}
