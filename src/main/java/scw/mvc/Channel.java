package scw.mvc;

import java.lang.reflect.Type;

import scw.core.ValueFactory;
import scw.core.attribute.Attributes;
import scw.core.parameter.ParameterConfig;
import scw.core.parameter.ParameterFactory;
import scw.logger.LogService;

public interface Channel extends LogService, Attributes<Object>, ParameterFactory<ParameterConfig>, ValueFactory<String>{
	long getCreateTime();

	void write(Object obj) throws Throwable;

	<T> T getBean(String name);
	
	<T> T getBean(Class<T> type);
	
	<T> T getObject(Class<T> type);
	
	<T> T getObject(Type type);
}
