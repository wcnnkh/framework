package io.basc.framework.netflix.hystrix;

import com.netflix.hystrix.HystrixCommand;

import io.basc.framework.util.reflect.MethodInvoker;

public interface HystrixCommandFactory {
	HystrixCommand<?> getHystrixCommandFactory(MethodInvoker invoker, Object[] args)
			throws Exception;
}
