package io.basc.framework.mvc;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterFactory;
import io.basc.framework.instance.NoArgsInstanceFactory;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mvc.security.UserSessionFactoryAdapter;
import io.basc.framework.mvc.security.UserSessionResolver;
import io.basc.framework.security.session.UserSession;
import io.basc.framework.value.ValueFactory;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.message.WebMessageConverters;
import io.basc.framework.web.message.WebMessagelConverterException;

import java.io.IOException;

public interface HttpChannel
		extends ParameterFactory, UserSessionFactoryAdapter, ValueFactory<String>, NoArgsInstanceFactory {
	static final String UID_ATTRIBUTE = "mvc.http.channel.uid";
	static final String SESSIONID_ATTRIBUTE = "mvc.http.channel.sessionid";

	long getCreateTime();

	ServerHttpRequest getRequest();

	ServerHttpResponse getResponse();

	WebMessageConverters getMessageConverters();

	boolean isCompleted();

	UserSessionResolver getUserSessionResolver();

	@Nullable
	<T> T getUid(Class<T> type);

	@Nullable
	String getSessionId();

	@Nullable
	<T> UserSession<T> getUserSession(Class<T> type);

	<T> UserSession<T> createUserSession(Class<T> type, T uid, String sessionId);

	void write(TypeDescriptor type, Object body) throws IOException, WebMessagelConverterException;
}
