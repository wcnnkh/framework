package scw.aop;

import scw.aop.support.ConfigurableMethodInterceptor;


public interface ConfigurableAop extends Aop{
	ConfigurableMethodInterceptor getMethodInterceptor(); 
	
	void addAopPolicy(AopPolicy aopPolicy);
}
