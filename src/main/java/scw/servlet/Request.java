package scw.servlet;

import javax.servlet.ServletRequest;

import scw.core.StringVerification;
import scw.core.ValueFactory;
import scw.logger.LogService;

public interface Request extends ServletRequest, LogService, StringVerification, ValueFactory<String> {
	long getCreateTime();

	<T> T getBean(Class<T> type);

	<T> T getBean(String name);

	<T> T getObject(Class<T> type);
}
