package scw.context.support;

import scw.aop.ProxyFactory;
import scw.aop.support.DefaultConfigurableAop;
import scw.context.transaction.TransactionMethodInterceptor;

public class ContextAop extends DefaultConfigurableAop{

	public ContextAop(ProxyFactory proxyFactory) {
		super(proxyFactory);
		getMethodInterceptor().addFirstMethodInterceptor(new TransactionMethodInterceptor());
	}

}
