package scw.net.http.server.mvc;

import scw.core.parameter.ParameterFactory;
import scw.logger.LogService;
import scw.net.http.server.ServerHttpRequest;
import scw.net.http.server.ServerHttpResponse;
import scw.net.http.server.mvc.beans.HttpChannelBeanManager;
import scw.value.ValueFactory;

public interface HttpChannel extends LogService, ParameterFactory, ValueFactory<String> {
	long getCreateTime();
	
	ServerHttpRequest getRequest();

	ServerHttpResponse getResponse();

	<E> E[] getArray(String name, Class<? extends E> type);
	
	boolean isCompleted();
	
	HttpChannelBeanManager getHttpChannelBeanManager();
}
