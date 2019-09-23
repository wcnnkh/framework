package scw.security.login;

import scw.beans.annotation.AutoImpl;
import scw.security.token.UserToken;

@AutoImpl(DefaultLoginFactory.class)
public interface LoginFactory<T> {
	UserToken<T> getUserToken(String token);

	UserToken<T> getUserTokenByUid(T uid);

	UserToken<T> login(T uid);

	UserToken<T> login(String token, T uid);

	void cancelLogin(String token);

	void cancelLoginByUid(T uid);
}
