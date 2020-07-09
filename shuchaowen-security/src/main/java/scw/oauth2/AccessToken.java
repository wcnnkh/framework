package scw.oauth2;

import java.io.Serializable;

import scw.mapper.Cloneable;
import scw.security.Token;

public class AccessToken implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	private Token accessToken;
	private TokenType tokenType;
	private Token refreshToken;
	private String scope;
	private String state;

	public Token getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(Token accessToken) {
		this.accessToken = accessToken;
	}

	public TokenType getTokenType() {
		return tokenType;
	}

	public void setTokenType(TokenType tokenType) {
		this.tokenType = tokenType;
	}

	public Token getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(Token refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Override
	public AccessToken clone() {
		AccessToken accessToken = new AccessToken();
		accessToken.accessToken = this.accessToken.clone();
		accessToken.refreshToken = this.refreshToken.clone();
		accessToken.scope = this.scope;
		accessToken.tokenType = this.tokenType;
		accessToken.state = this.state;
		return accessToken;
	}

}
