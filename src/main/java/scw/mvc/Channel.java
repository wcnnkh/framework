package scw.mvc;

import scw.core.attribute.Attributes;
import scw.core.reflect.ParameterFactory;
import scw.logger.LogService;

public interface Channel extends LogService, Attributes<Object>, ParameterFactory{
	long getCreateTime();

	void write(Object obj) throws Throwable;
	
	<T> T getBean(Class<T> type);
}
