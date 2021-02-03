package scw.aop;

public interface ConfigurableProxyFactory extends ProxyFactory{
	void addProxyFactory(ProxyFactory proxyFactory);
}
