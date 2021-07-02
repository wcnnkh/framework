package scw.aop.support;

import java.util.Iterator;

import scw.aop.AopPolicy;
import scw.aop.ConfigurableAop;
import scw.aop.ProxyFactory;
import scw.instance.Configurable;
import scw.instance.ServiceList;
import scw.instance.ServiceLoaderFactory;

public class DefaultConfigurableAop extends AbstractAop implements ConfigurableAop, Configurable {
	private final ProxyFactory proxyFactory;
	private final ConfigurableMethodInterceptor configurableMethodInterceptor = new ConfigurableMethodInterceptor();
	private final ServiceList<AopPolicy> policies = new ServiceList<>(AopPolicy.class);

	public DefaultConfigurableAop() {
		this(ProxyUtils.getFactory());
	}

	public DefaultConfigurableAop(ProxyFactory proxyFactory) {
		this.proxyFactory = proxyFactory;
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		configurableMethodInterceptor.configure(serviceLoaderFactory);
		policies.configure(serviceLoaderFactory);
	}

	@Override
	public ProxyFactory getProxyFactory() {
		return proxyFactory;
	}

	public ConfigurableMethodInterceptor getMethodInterceptor() {
		return configurableMethodInterceptor;
	}

	public void addAopPolicy(AopPolicy aopPolicy) {
		policies.addService(aopPolicy);
	}

	public Iterator<AopPolicy> getPolicyIterator() {
		return policies.iterator();
	}

	public boolean isProxy(Object instance) {
		if (instance == null) {
			return false;
		}

		Iterator<AopPolicy> iterator = getPolicyIterator();
		while (iterator.hasNext()) {
			AopPolicy aopPolicy = iterator.next();
			if (aopPolicy.isProxy(instance)) {
				return true;
			}
		}
		return super.isProxy(instance);
	}
}
