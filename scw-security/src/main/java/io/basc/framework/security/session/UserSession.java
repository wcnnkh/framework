package io.basc.framework.security.session;

public interface UserSession<T> extends Session {
	T getUid();
}
