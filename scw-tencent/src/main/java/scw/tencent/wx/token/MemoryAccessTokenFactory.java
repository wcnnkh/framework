package scw.tencent.wx.token;

import scw.core.parameter.annotation.ParameterName;
import scw.oauth2.AccessToken;
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
		if (isExpired()) {
			synchronized (lock) {
				if (isExpired()) {
					this.accessToken = WeiXinUtils.getAccessToken(getAppId(), getAppSecret());
				}
			}
		}
		return accessToken;
	}

}
