package io.basc.framework.mvc;

import java.io.IOException;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.factory.InstanceFactory;
import io.basc.framework.factory.ParameterFactory;
import io.basc.framework.lang.Nullable;
import io.basc.framework.logger.Logger;
import io.basc.framework.security.login.UserToken;
import io.basc.framework.security.session.UserSession;
import io.basc.framework.value.ValueFactory;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.message.WebMessageConverters;
import io.basc.framework.web.message.WebMessagelConverterException;

public interface HttpChannel extends ParameterFactory, ValueFactory<String>, InstanceFactory {
	long getCreateTime();

	ServerHttpRequest getRequest();

	ServerHttpResponse getResponse();

	WebMessageConverters getMessageConverters();

	boolean isCompleted();

	void write(TypeDescriptor type, Object body) throws IOException, WebMessagelConverterException;

	@Nullable
	<T> UserToken<T> getUserToken(Class<T> type);

	@Nullable
	<T> UserSession<T> getUserSession(Class<T> type);

	<T> UserSession<T> createUserSession(T uid);

	Logger getLogger();

	void setLogger(Logger logger);
}
