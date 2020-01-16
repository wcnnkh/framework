package scw.rcp;

import scw.aop.Proxy;

public interface ProxyFactory {
	Proxy createProxy(Class<?> clazz);
}
