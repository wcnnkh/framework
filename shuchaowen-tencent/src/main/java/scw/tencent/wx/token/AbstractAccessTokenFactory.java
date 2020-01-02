package scw.tencent.wx.token;

import scw.tencent.wx.AccessToken;

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

	protected boolean isExpires() {
		AccessToken accessToken = getAccessTokenByCache();
		return accessToken == null || accessToken.isExpires();
	}

	public String getAccessToken() {
		AccessToken accessToken = getAccessTokenByCache();
		if (accessToken == null || accessToken.isExpires()) {
			accessToken = refreshToken();
		}

		if (accessToken == null) {
			throw new RuntimeException("无法获取token");
		}

		return accessToken.getAccess_token();
	}

	protected abstract AccessToken refreshToken();

	/**
	 * 从缓存中获取ticket
	 * 
	 * @return
	 */
	protected abstract AccessToken getAccessTokenByCache();
}
