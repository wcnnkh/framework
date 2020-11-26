package scw.tencent.wx;

import scw.core.instance.annotation.Configuration;
import scw.core.parameter.annotation.ParameterName;
import scw.data.TemporaryCache;
import scw.oauth2.AccessToken;
import scw.security.Token;

@Configuration(order=Integer.MIN_VALUE + 1)
public class TemporaryCacheTokenFactory extends DefaultTokenFactory{
	private final TemporaryCache temporaryCache;
	
	public TemporaryCacheTokenFactory(@ParameterName(WX_APPID_KEY) String appId, @ParameterName(WX_APPSECRET_KEY)String appSecret, TemporaryCache temporaryCache) {
		super(appId, appSecret);
		this.temporaryCache = temporaryCache;
	}

	@Override
	public AccessToken getAccessToken(String type) {
		String key = "wx-token:" + getAppId() + ":" + type;
		AccessToken accessToken = temporaryCache.get(key);
		if(accessToken == null || accessToken.getToken().isExpired(getTokenExpireAheadTime())){
			accessToken = super.getAccessToken(type);
			temporaryCache.set(key, accessToken.getToken().getExpiresIn() - getTokenExpireAheadTime(), accessToken);
		}
		return accessToken;
	}
	
	@Override
	public Token getTicket(String type) {
		String key = "wx-ticket:" + getAppId() + ":" +type;
		Token token = temporaryCache.get(key);
		if(token == null || token.isExpired(getTokenExpireAheadTime())){
			token = super.getTicket(type);
			temporaryCache.set(key, token.getExpiresIn() - getTokenExpireAheadTime(), token);
		}
		return token;
	}
}
