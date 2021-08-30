package io.basc.framework.netflix.hystrix;

import io.basc.framework.beans.annotation.AutoImpl;
import io.basc.framework.reflect.MethodInvoker;

import com.netflix.hystrix.HystrixCommand;

@AutoImpl(DefaultHystrixCommandFactory.class)
public interface HystrixCommandFactory {
	HystrixCommand<?> getHystrixCommandFactory(MethodInvoker invoker, Object[] args)
			throws Exception;
}
