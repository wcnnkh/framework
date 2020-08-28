package scw.security;

import scw.security.login.UserToken;
import scw.security.session.Session;

/**
 * 授权
 * @author shuchaowen
 *
 * @param <T>
 */

public interface Authorization<T> {
	T getUid();
	
	Session getSession();
	
	UserToken<T> authorization(T uid);
}
