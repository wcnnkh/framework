package scw.netflix.hystrix;

import scw.aop.MethodInterceptorChain;
import scw.beans.annotation.AutoImpl;
import scw.core.reflect.MethodInvoker;

import com.netflix.hystrix.HystrixCommand;

@AutoImpl(DefaultHystrixCommandFactory.class)
public interface HystrixCommandFactory {
	HystrixCommand<?> getHystrixCommandFactory(MethodInvoker invoker, Object[] args, MethodInterceptorChain chain)
			throws Exception;
}
