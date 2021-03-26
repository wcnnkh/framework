package scw.rpc.remote;

import java.lang.reflect.Method;
import java.util.Enumeration;

import scw.lang.NamedThreadLocal;

public abstract class RemoteRequestMessage extends DefaultRemoteMessageHeaders {
	private static final long serialVersionUID = 1L;
	private static ThreadLocal<RemoteMessageHeaders> HEADERS_LOCAL = new NamedThreadLocal<RemoteMessageHeaders>(
			"REMOTE_REQUEST_MESSAGE_HEADERS");

	public static void setLocalHeaders(RemoteMessageHeaders headers) {
		if (headers == null) {
			HEADERS_LOCAL.remove();
		} else {
			HEADERS_LOCAL.set(headers);
		}
	}

	public RemoteRequestMessage() {
		RemoteMessageHeaders headers = HEADERS_LOCAL.get();
		if (headers != null) {
			Enumeration<String> keys = headers.getAttributeNames();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				setAttribute(key, headers.getAttribute(key));
			}
		}
	}

	public abstract Class<?> getTargetClass();

	public abstract Method getMethod();

	public abstract Object[] getArgs();
}
