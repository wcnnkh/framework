package scw.aop.support;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import scw.aop.AopPolicy;
import scw.aop.ConfigurableAop;
import scw.aop.MethodInterceptor;
import scw.aop.ProxyFactory;
import scw.core.utils.CollectionUtils;
import scw.instance.ServiceLoader;
import scw.instance.ServiceLoaderFactory;

public class DefaultConfigurableAop extends AbstractAop implements
		ConfigurableAop {
	private final ProxyFactory proxyFactory;
	private final ConfigurableMethodInterceptor configurableMethodInterceptor = new ConfigurableMethodInterceptor();
	private final List<AopPolicy> policies = new CopyOnWriteArrayList<AopPolicy>();

	public DefaultConfigurableAop(){
		this(ProxyUtils.getFactory());
	}

	public DefaultConfigurableAop(ProxyFactory proxyFactory) {
		this.proxyFactory = proxyFactory;
	}

	@Override
	public ProxyFactory getProxyFactory() {
		return proxyFactory;
	}

	public ConfigurableMethodInterceptor getMethodInterceptor() {
		return configurableMethodInterceptor;
	}

	public void addAopPolicy(AopPolicy aopPolicy) {
		synchronized (policies) {
			policies.add(aopPolicy);
		}
	}

	public Iterator<AopPolicy> getPolicyIterator() {
		return CollectionUtils.getIterator(policies, true);
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

	private final AtomicBoolean loaded = new AtomicBoolean();

	public boolean loadServices(ServiceLoaderFactory serviceLoaderFactory) {
		if (loaded.compareAndSet(false, true)) {
			ServiceLoader<MethodInterceptor> interceptorLoader = serviceLoaderFactory
					.getServiceLoader(MethodInterceptor.class);
			configurableMethodInterceptor
					.addMethodInterceptor(new UnmodifiableMethodInterceptors(
							interceptorLoader));

			for (AopPolicy aopPolicy : serviceLoaderFactory
					.getServiceLoader(AopPolicy.class)) {
				addAopPolicy(aopPolicy);
			}
			return true;
		}
		return false;
	}
}
