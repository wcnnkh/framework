package scw.rcp;

import java.lang.reflect.Method;
import java.util.Collection;

import scw.core.utils.SystemPropertyUtils;
import scw.net.NetworkUtils;
import scw.net.message.converter.MessageConverter;

public class HttpRPCConfigure implements RPCConfigure {
	public String getProperty(String key) {
		return SystemPropertyUtils.getProperty(key);
	}

	public Collection<MessageConverter> getMessageConveters(Class<?> clazz, Method method) {
		return NetworkUtils.getMessageConverters();
	}

	public String getRequestUrl(Class<?> clazz, Method method) {
		return null;
	}
}
