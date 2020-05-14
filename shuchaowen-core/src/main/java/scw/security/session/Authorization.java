package scw.security.session;

import scw.security.login.UserToken;

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
