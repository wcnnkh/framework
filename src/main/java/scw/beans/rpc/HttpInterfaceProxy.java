package scw.beans.rpc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class HttpInterfaceProxy implements InvocationHandler {
	private final String host;

	public HttpInterfaceProxy(String host) {
		this.host = host;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		HttpProxyDefinition httpProxyDefinition = new HttpProxyDefinition(method);
		// TODO Auto-generated method stub
		return null;
	}

}
