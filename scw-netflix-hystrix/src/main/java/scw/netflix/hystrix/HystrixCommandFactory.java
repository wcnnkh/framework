package scw.netflix.hystrix;

import scw.aop.MethodInterceptorChain;
import scw.aop.MethodInvoker;
import scw.beans.annotation.AutoImpl;

import com.netflix.hystrix.HystrixCommand;

@AutoImpl(DefaultHystrixCommandFactory.class)
public interface HystrixCommandFactory {
	HystrixCommand<?> getHystrixCommandFactory(MethodInvoker invoker, Object[] args, MethodInterceptorChain chain)
			throws Exception;
}
