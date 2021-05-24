package scw.mvc;

import java.io.IOException;

import scw.convert.TypeDescriptor;
import scw.core.parameter.ParameterDescriptor;
import scw.core.parameter.ParameterFactory;
import scw.instance.NoArgsInstanceFactory;
import scw.lang.Nullable;
import scw.mvc.security.UserSessionFactoryAdapter;
import scw.mvc.security.UserSessionResolver;
import scw.security.session.UserSession;
import scw.value.ValueFactory;
import scw.web.ServerHttpRequest;
import scw.web.ServerHttpResponse;
import scw.web.message.WebMessageConverters;
import scw.web.message.WebMessagelConverterException;

public interface HttpChannel extends ParameterFactory, UserSessionFactoryAdapter, ValueFactory<String> {
	static final String UID_ATTRIBUTE = "mvc.http.channel.uid";
	static final String SESSIONID_ATTRIBUTE = "mvc.http.channel.sessionid";
	
	long getCreateTime();

	ServerHttpRequest getRequest();

	ServerHttpResponse getResponse();
	
	WebMessageConverters getMessageConverters();

	boolean isCompleted();

	Object getParameter(ParameterDescriptor parameterDescriptor) throws IOException;
	
	NoArgsInstanceFactory getInstanceFactory();
	
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
