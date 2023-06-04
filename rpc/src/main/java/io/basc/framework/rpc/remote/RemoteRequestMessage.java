package io.basc.framework.rpc.remote;

import java.lang.reflect.Method;
import java.util.Enumeration;

import io.basc.framework.beans.factory.config.InheritableThreadLocalConfigurator;

public abstract class RemoteRequestMessage extends DefaultRemoteMessageHeaders {
	private static final long serialVersionUID = 1L;
	private static final InheritableThreadLocalConfigurator<RemoteMessageHeaders> HEADERS_CONFIGURATOR = new InheritableThreadLocalConfigurator<RemoteMessageHeaders>(
			RemoteMessageHeaders.class);

	public static InheritableThreadLocalConfigurator<RemoteMessageHeaders> getHeadersConfigurator() {
		return HEADERS_CONFIGURATOR;
	}

	public RemoteRequestMessage() {
		RemoteMessageHeaders headers = HEADERS_CONFIGURATOR.get();
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
