package scw.tencent.wx;

import scw.oauth2.AccessToken;
import scw.oauth2.TokenType;
import scw.security.Token;

public class UserAccessToken extends AccessToken {
	private static final long serialVersionUID = 1L;
	private final String openid;

	public UserAccessToken(AccessToken accessToken, String openid) {
		super(accessToken);
		this.openid = openid;
	}

	public UserAccessToken(Token accessToken, TokenType tokenType, Token refreshToken, String scope, String state,
			String openid) {
		super(accessToken, tokenType, refreshToken, scope, state);
		this.openid = openid;
	}

	public String getOpenid() {
		return openid;
	}

	@Override
	public UserAccessToken clone() {
		return new UserAccessToken(super.clone(), openid);
	}
}
