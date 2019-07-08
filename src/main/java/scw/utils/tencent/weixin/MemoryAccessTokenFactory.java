package scw.utils.tencent.weixin;

public final class MemoryAccessTokenFactory extends AbstractAccessTokenFactory {
	private volatile AccessToken accessToken;
	private volatile Object lock = new Object();

	public MemoryAccessTokenFactory(String appid, String appsecret) {
		super(appid, appsecret);
	}

	@Override
	protected AccessToken getAccessTokenByCache() {
		return accessToken;
	}

	@Override
	protected AccessToken refreshToken() {
		if (isExpires()) {
			synchronized (lock) {
				if (isExpires()) {
					AccessToken accessToken = WeiXinUtils.getAccessToken(getAppId(), getAppSecret());
					if (accessToken.isSuccess()) {
						this.accessToken = accessToken;
					}
				}
			}
		}
		return accessToken;
	}

}
