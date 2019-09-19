package scw.security.token;

import java.io.Serializable;

public final class SimpleUserToken<T> implements UserToken<T>, Serializable {
	private static final long serialVersionUID = 1L;
	private String token;
	private T uid;

	@SuppressWarnings("unused")
	private SimpleUserToken() {
	};

	public SimpleUserToken(String token, T uid) {
		this.token = token;
		this.uid = uid;
	}

	public T getUid() {
		return uid;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setUid(T uid) {
		this.uid = uid;
	}

}
