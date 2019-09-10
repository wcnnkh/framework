package scw.session;

public interface UserSession<T> extends Session {
	T getUid();
}
