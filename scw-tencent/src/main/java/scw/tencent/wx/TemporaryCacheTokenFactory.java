package scw.tencent.wx;

import scw.data.TemporaryStorage;
import scw.oauth2.AccessToken;
import scw.security.Token;

public class TemporaryCacheTokenFactory extends DefaultTokenFactory{
	private final TemporaryStorage temporaryCache;
	
	public TemporaryCacheTokenFactory(String appId, String appSecret, TemporaryStorage temporaryCache) {
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
