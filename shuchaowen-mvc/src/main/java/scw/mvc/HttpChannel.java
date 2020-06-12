package scw.mvc;

import scw.core.parameter.ParameterFactory;
import scw.http.server.ServerHttpRequest;
import scw.http.server.ServerHttpResponse;
import scw.logger.LogService;
import scw.mvc.beans.HttpChannelBeanManager;
import scw.value.Value;

public interface HttpChannel extends LogService, ParameterFactory {
	long getCreateTime();
	
	ServerHttpRequest getRequest();

	ServerHttpResponse getResponse();

	<E> E[] getArray(String name, Class<? extends E> type);
	
	boolean isCompleted();
	
	HttpChannelBeanManager getHttpChannelBeanManager();
	
	Value getValue(String name);
	
	Value getValue(String name, Value defaultValue);
}
