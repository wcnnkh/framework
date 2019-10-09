package scw.mvc;

import scw.core.attribute.AttributeManager;
import scw.core.parameter.ParameterFactory;
import scw.logger.LogService;

public interface Channel extends LogService, AttributeManager, ParameterFactory{
	long getCreateTime();

	void write(Object obj) throws Throwable;
	
	<T> T getBean(Class<T> type);
}
