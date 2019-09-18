package scw.security.session;

/**
 * 授权
 * @author shuchaowen
 *
 * @param <T>
 */

public interface Authorization<T> {
	T getUid();
	
	Session getSession();
	
	void authorization(T uid);
}
