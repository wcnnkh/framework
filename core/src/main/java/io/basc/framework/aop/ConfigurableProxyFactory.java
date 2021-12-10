package io.basc.framework.aop;

public interface ConfigurableProxyFactory extends ProxyFactory {
	void addProxyFactory(ProxyFactory proxyFactory);
}
