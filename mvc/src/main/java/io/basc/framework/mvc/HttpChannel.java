package io.basc.framework.mvc;

import java.io.IOException;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterFactory;
import io.basc.framework.factory.NoArgsInstanceFactory;
import io.basc.framework.lang.Nullable;
import io.basc.framework.logger.Logger;
import io.basc.framework.mvc.security.UserSessionManager;
import io.basc.framework.security.login.UserToken;
import io.basc.framework.security.session.UserSession;
import io.basc.framework.value.ValueFactory;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.message.WebMessageConverter;
import io.basc.framework.web.message.WebMessageConverters;
import io.basc.framework.web.message.WebMessagelConverterException;

public interface HttpChannel extends ParameterFactory, ValueFactory<String>, NoArgsInstanceFactory {
	long getCreateTime();

	ServerHttpRequest getRequest();

	ServerHttpResponse getResponse();

	WebMessageConverters getMessageConverters();
	
	boolean isCompleted();

	/**
	 * @see WebMessageConverter#write(TypeDescriptor, Object, ServerHttpRequest,
	 *      ServerHttpResponse)
	 * @param type
	 * @param body
	 * @throws IOException
	 * @throws WebMessagelConverterException
	 */
	void write(TypeDescriptor type, Object body) throws IOException, WebMessagelConverterException;

	/**
	 * 解析这个请求的user token
	 * 
	 * @see UserSessionManager#read(HttpChannel, Class)
	 * @param <T>
	 * @param type
	 * @return
	 */
	@Nullable
	<T> UserToken<T> getUserToken(Class<T> type);

	/**
	 * 获取这个channel对应的session
	 * 
	 * @see UserSessionManager#getUserSession(HttpChannel, Class)
	 * @param <T>
	 * @param type
	 * @return
	 */
	@Nullable
	<T> UserSession<T> getUserSession(Class<T> type);

	/**
	 * 创建一个session
	 * 
	 * @see UserSessionManager#createUserSession(HttpChannel, Object)
	 * @param <T>
	 * @param uid
	 * @return
	 */
	<T> UserSession<T> createUserSession(T uid);
	
	Logger getLogger();
	
	void setLogger(Logger logger);
}
