package io.basc.framework.security.login;

import io.basc.framework.beans.annotation.AutoImpl;

@AutoImpl(DefaultLoginService.class)
public interface LoginService<T> {
	UserToken<T> getUserToken(String token);

	UserToken<T> getUserTokenByUid(T uid);

	UserToken<T> login(T uid);

	boolean cancelLogin(String token);

	boolean cancelLoginByUid(T uid);
	
	boolean verification(String token, T uid);
}
