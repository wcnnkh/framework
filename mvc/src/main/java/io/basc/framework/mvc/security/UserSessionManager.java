package io.basc.framework.mvc.security;

import io.basc.framework.lang.Nullable;
import io.basc.framework.mvc.HttpChannel;
import io.basc.framework.security.login.UserToken;
import io.basc.framework.security.session.UserSession;

/**
 * 用户session管理
 * 
 * @author wcnnkh
 *
 */
public interface UserSessionManager {
	<T> UserToken<T> read(HttpChannel httpChannel, Class<T> type);

	<T> void write(HttpChannel httpChannel, UserToken<T> userToken);

	@Nullable
	<T> UserSession<T> getUserSession(HttpChannel httpChannel, Class<T> type);

	<T> UserSession<T> createUserSession(HttpChannel httpChannel, T uid);
}
