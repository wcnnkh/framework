package scw.mvc;

import scw.core.parameter.ParameterFactory;
import scw.logger.LogService;
import scw.mvc.beans.ChannelBeanFactory;
import scw.net.http.server.ServerHttpRequest;
import scw.net.http.server.ServerHttpResponse;
import scw.util.attribute.Attributes;
import scw.value.ValueFactory;

public interface Channel extends LogService, Attributes<String, Object>, ParameterFactory, ValueFactory<String>, ChannelBeanFactory {
	long getCreateTime();

	ServerHttpRequest getRequest();

	ServerHttpResponse getResponse();

	boolean isSupportAsyncControl();

	AsyncControl getAsyncControl();
	
	<E> E[] getArray(String name, Class<? extends E> type);
	
	boolean isCompleted();
}
