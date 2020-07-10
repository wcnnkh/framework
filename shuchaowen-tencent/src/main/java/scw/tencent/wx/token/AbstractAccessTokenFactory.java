package scw.tencent.wx.token;

import scw.oauth2.AccessToken;

public abstract class AbstractAccessTokenFactory implements AccessTokenFactory {
	private final String appId;
	private final String appSecret;

	public AbstractAccessTokenFactory(String appId, String appSecret) {
		this.appId = appId;
		this.appSecret = appSecret;
	}

	public String getAppId() {
		return appId;
	}

	public String getAppSecret() {
		return appSecret;
	}

	protected boolean isExpired() {
		AccessToken accessToken = getAccessTokenByCache();
		return accessToken == null || accessToken.getAccessToken().isExpired();
	}

	public String getAccessToken() {
		AccessToken accessToken = getAccessTokenByCache();
		if (accessToken == null || accessToken.getAccessToken().isExpired()) {
			accessToken = refreshToken();
		}

		if (accessToken == null) {
			throw new RuntimeException("无法获取token");
		}

		return accessToken.getAccessToken().getToken();
	}

	protected abstract AccessToken refreshToken();

	/**
	 * 从缓存中获取ticket
	 * 
	 * @return
	 */
	protected abstract AccessToken getAccessTokenByCache();
}
