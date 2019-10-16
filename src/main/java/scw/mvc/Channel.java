package scw.mvc;

import scw.core.attribute.Attributes;
import scw.core.parameter.ParameterConfig;
import scw.core.parameter.ParameterFactory;
import scw.logger.LogService;

public interface Channel extends LogService, Attributes<Object>, ParameterFactory<ParameterConfig> {
	long getCreateTime();

	void write(Object obj) throws Throwable;

	<T> T getBean(Class<T> type);
}
