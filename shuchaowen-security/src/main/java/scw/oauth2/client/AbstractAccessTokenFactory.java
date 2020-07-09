package scw.oauth2.client;

import scw.data.TemporaryCache;
import scw.oauth2.AccessToken;

public abstract class AbstractAccessTokenFactory implements AccessTokenFactory {
	private final TemporaryCache temporaryCache;
	private final String cacheName;

	public AbstractAccessTokenFactory(TemporaryCache temporaryCache, String cacheName) {
		this.temporaryCache = temporaryCache;
		this.cacheName = cacheName;
	}

	public AccessToken getAccessToken() {
		AccessToken accessToken = temporaryCache.get(cacheName);
		if (accessToken == null || accessToken.getAccessToken().isExpired()) {
			accessToken = getNewAccessToken(accessToken);
			temporaryCache.set(cacheName, accessToken.getAccessToken().getExpiresIn(), accessToken.clone());
		}
		return accessToken;
	}

	public abstract AccessToken getNewAccessToken(AccessToken oldAccessToken);
}
