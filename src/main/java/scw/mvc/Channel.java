package scw.mvc;

import scw.logger.LogService;

public interface Channel extends LogService, AttributeManager{
	long getCreateTime();

	Object getParameter(ParameterDefinition parameterDefinition);

	void write(Object obj) throws Throwable;
	
	<T> T getBean(Class<T> type);
}
