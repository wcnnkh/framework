package scw.security.token;

public interface UserToken<T> {
	T getUid();

	String getToken();
}
