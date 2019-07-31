package scw.beans.rpc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URLEncoder;

import scw.core.utils.ClassUtils;

//TODO
public class HttpInterfaceProxy implements InvocationHandler {
	private final String host;
	private final String charsetName;

	public HttpInterfaceProxy(String host, String charsetName) {
		this.host = host;
		this.charsetName = charsetName;
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		// TODO Auto-generated method stub
		return null;
	}

	protected void doGet(Method method, Object[] args) throws Exception {
		String[] names = ClassUtils.getParameterName(method);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < names.length; i++) {
			sb.append(URLEncoder.encode(names[i], charsetName));
		}
	}

	public final String getHost() {
		return host;
	}

	public final String getCharsetName() {
		return charsetName;
	}
}
