package io.basc.framework.oauth2;

import java.io.Serializable;

import io.basc.framework.security.Token;
import lombok.Data;

@Data
public class AccessToken implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	private final Token token;
	private final String type;
	private final Token refreshToken;
	private final String scope;
	private final String state;

	/**
	 * 方便序列化使用
	 */
	protected AccessToken() {
		this(null, null, null, null, null);
	}

	protected AccessToken(AccessToken accessToken) {
		this.token = accessToken.token;
		this.type = accessToken.type;
		this.refreshToken = accessToken.refreshToken;
		this.scope = accessToken.scope;
		this.state = accessToken.state;
	}

	/**
	 * @param accessToken
	 * @param type         For example: bearer、mac 等
	 * @param refreshToken
	 * @param scope
	 * @param state
	 */
	public AccessToken(Token accessToken, String type, Token refreshToken, String scope, String state) {
		this.token = accessToken;
		this.type = type;
		this.refreshToken = refreshToken;
		this.scope = scope;
		this.state = state;
	}

	public Token getToken() {
		return token;
	}

	public String getType() {
		return type;
	}

	public Token getRefreshToken() {
		return refreshToken;
	}

	public String getScope() {
		return scope;
	}

	public String getState() {
		return state;
	}

	@Override
	public AccessToken clone() {
		return new AccessToken(this.token == null ? null : token.clone(), type,
				this.refreshToken == null ? null : refreshToken.clone(), scope, state);
	}
}
