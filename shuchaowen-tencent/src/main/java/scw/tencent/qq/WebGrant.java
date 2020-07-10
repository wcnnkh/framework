package scw.tencent.qq;

import scw.lang.NotSupportedException;
import scw.oauth2.AccessToken;
import scw.oauth2.client.AuthorizationCodeClient;

public class WebGrant implements AuthorizationCodeClient {
	private final Configuration configuration;

	public WebGrant(Configuration configuration) {
		this.configuration = configuration;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public AccessToken refreshAccessToken(AccessToken accessToken, String scope) {
		throw new NotSupportedException("refreshAccessToken");
	}

	public String getClientId() {
		return configuration.getAppId();
	}

	public AccessToken getAccessToken(String code, String redirect_uri) {
		return QQUtils.getAccessToken(configuration.getAppId(), configuration.getAppKey(), redirect_uri, code);
	}

	public Userinfo getUserinfo(String code, String redirect_uri) {
		AccessToken accessToken = getAccessToken(code, redirect_uri);
		return QQUtils.getUserinfo(configuration.getAppId(), accessToken.getAccessToken().getToken(),
				QQUtils.getOpenId(accessToken.getAccessToken().getToken()));
	}
}
