package scw.aop;

import java.util.Collection;

public abstract class AbsttractProxyAdapter implements ProxyAdapter {

	public Proxy proxy(Class<?> clazz, Class<?>[] interfaces, Collection<? extends Filter> filters) {
		return proxy(clazz, interfaces, filters, null);
	}
}
