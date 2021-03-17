package scw.context.support;

import scw.aop.ProxyFactory;
import scw.aop.support.DefaultConfigurableAop;
import scw.context.transaction.TransactionMethodInterceptor;

public class DefaultAop extends DefaultConfigurableAop{

	public DefaultAop(ProxyFactory proxyFactory) {
		super(proxyFactory);
		getMethodInterceptor().addFirstMethodInterceptor(new TransactionMethodInterceptor());
	}

}
