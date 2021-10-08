package io.basc.framework.mvc.security;

import io.basc.framework.lang.Nullable;
import io.basc.framework.mvc.HttpChannel;
import io.basc.framework.security.login.UserToken;
import io.basc.framework.security.session.UserSession;

/**
 * 用户session管理
 * 
 * @author shuchaowen
 *
 */
public interface UserSessionManager {
	/**
	 * 读取token
	 * 
	 * @param <T>
	 * @param httpChannel
	 * @param type
	 * @return
	 */
	<T> UserToken<T> read(HttpChannel httpChannel, Class<T> type);

	/**
	 * 写入token
	 * 
	 * @param <T>
	 * @param httpChannel
	 * @param userToken
	 */
	<T> void write(HttpChannel httpChannel, UserToken<T> userToken);

	/**
	 * 获取请求的session
	 * 
	 * @param <T>
	 * @param httpChannel
	 * @param type
	 * @return
	 */
	@Nullable
	<T> UserSession<T> getUserSession(HttpChannel httpChannel, Class<T> type);

	/**
	 * 获取或创建一个session，如果指定的session不存在就创建一个session
	 * 
	 * @param <T>
	 * @param httpChannel
	 * @param userToken
	 * @return
	 */
	<T> UserSession<T> createUserSession(HttpChannel httpChannel, T uid);
}
