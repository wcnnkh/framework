package io.basc.framework.aop.support;

import io.basc.framework.aop.AopPolicy;
import io.basc.framework.aop.ConfigurableAop;
import io.basc.framework.aop.ProxyFactory;
import io.basc.framework.factory.Configurable;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.factory.ServiceLoaderFactory;

import java.util.Iterator;

public class DefaultConfigurableAop extends AbstractAop implements ConfigurableAop, Configurable {
	private final ProxyFactory proxyFactory;
	private final ConfigurableMethodInterceptor configurableMethodInterceptor = new ConfigurableMethodInterceptor();
	private final ConfigurableServices<AopPolicy> policies = new ConfigurableServices<>(AopPolicy.class);

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
		policies.register(aopPolicy);
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

	@Override
	public boolean isConfigured() {
		return configurableMethodInterceptor.isConfigured() && policies.isConfigured();
	}
}
