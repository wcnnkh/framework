package scw.tencent.wx.token;

import scw.core.annotation.ParameterName;
import scw.tencent.wx.AccessToken;
import scw.tencent.wx.WeiXinUtils;

public final class MemoryAccessTokenFactory extends AbstractAccessTokenFactory {
	private volatile AccessToken accessToken;
	private volatile Object lock = new Object();

	public MemoryAccessTokenFactory(@ParameterName(WX_APPID_KEY) String appid,
			@ParameterName(WX_APPSECRET_KEY) String appsecret) {
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
