package scw.integration.tencent.wx.token;

import scw.beans.annotation.AutoImpl;
import scw.integration.tencent.wx.WXConstants;

@AutoImpl({ MemcachedAccessTokenFactory.class, RedisAccessTokenFactory.class, MemoryAccessTokenFactory.class })
public interface AccessTokenFactory extends WXConstants{
	String getAppId();

	String getAppSecret();

	String getAccessToken();
}
