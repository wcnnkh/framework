package io.basc.framework.aop;

import io.basc.framework.aop.support.ConfigurableMethodInterceptor;


public interface ConfigurableAop extends Aop{
	ConfigurableMethodInterceptor getMethodInterceptor(); 
	
	void addAopPolicy(AopPolicy aopPolicy);
}
