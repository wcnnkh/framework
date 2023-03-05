package io.basc.framework.rpc.remote;

import java.lang.reflect.Method;
import java.util.Enumeration;

import io.basc.framework.lang.NamedInheritableThreadLocal;

public abstract class RemoteRequestMessage extends DefaultRemoteMessageHeaders {
	private static final long serialVersionUID = 1L;
	private static final ThreadLocal<RemoteMessageHeaders> HEADERS_LOCAL = new NamedInheritableThreadLocal<RemoteMessageHeaders>(
			RemoteMessageHeaders.class.getName(), true);

	public static ThreadLocal<RemoteMessageHeaders> getHeadersLocal() {
		return HEADERS_LOCAL;
	}

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
