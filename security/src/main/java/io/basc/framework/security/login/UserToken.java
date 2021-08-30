package io.basc.framework.security.login;

import java.io.Serializable;

public class UserToken<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	private String token;
	private T uid;

	public UserToken(String token, T uid) {
		this.token = token;
		this.uid = uid;
	}

	public T getUid() {
		return uid;
	}

	public String getToken() {
		return token;
	}
}
