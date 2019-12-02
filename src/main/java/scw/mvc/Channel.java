package scw.mvc;

import scw.core.parameter.ParameterConfig;
import scw.core.parameter.ParameterFactory;
import scw.logger.LogService;
import scw.util.attribute.Attributes;

public interface Channel extends LogService, Attributes<String, Object>, ParameterFactory<ParameterConfig>{
	long getCreateTime();

	void write(Object obj) throws Throwable;

	<T> T getBean(String name);
	
	<T> T getBean(Class<T> type);
	
	<T extends Request> T getRequest();
	
	<T extends Response> T getResponse();
}
