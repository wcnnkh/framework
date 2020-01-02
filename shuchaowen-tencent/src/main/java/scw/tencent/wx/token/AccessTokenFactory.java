package scw.tencent.wx.token;

import scw.beans.annotation.AutoImpl;
import scw.tencent.wx.WXConstants;

@AutoImpl({ MemcachedAccessTokenFactory.class, RedisAccessTokenFactory.class, MemoryAccessTokenFactory.class })
public interface AccessTokenFactory extends WXConstants{
	String getAppId();

	String getAppSecret();

	String getAccessToken();
}
