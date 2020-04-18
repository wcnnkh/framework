package scw.mvc;

import scw.core.parameter.ParameterFactory;
import scw.logger.LogService;
import scw.mvc.beans.ChannelBeanFactory;
import scw.util.attribute.Attributes;
import scw.util.value.ValueFactory;

public interface Channel extends LogService, Attributes<String, Object>, ParameterFactory, ValueFactory<String>, ChannelBeanFactory {
	long getCreateTime();

	<T extends Request> T getRequest();

	<T extends Response> T getResponse();

	boolean isSupportAsyncControl();

	AsyncControl getAsyncControl();
	
	<E> E[] getArray(String name, Class<? extends E> type);
	
	boolean isCompleted();
}
