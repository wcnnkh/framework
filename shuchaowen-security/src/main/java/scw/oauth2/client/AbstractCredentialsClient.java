package scw.oauth2.client;

import scw.data.TemporaryCache;
import scw.oauth2.AccessToken;

public abstract class AbstractCredentialsClient implements CredentialsClient {
	protected final TemporaryCache temporaryCache;

	public AbstractCredentialsClient(TemporaryCache temporaryCache) {
		this.temporaryCache = temporaryCache;
	}

	public AccessToken getAccessToken(String scope) {
		if (temporaryCache == null) {
			return getNewAccessToken(scope);
		}

		String key = getTemporaryCacheKey(scope);
		AccessToken accessToken = temporaryCache.get(key);
		if (accessToken == null || accessToken.getAccessToken().isExpired()) {
			if (accessToken != null && accessToken.getRefreshToken() != null
					&& !accessToken.getRefreshToken().isExpired()) {
				accessToken = refreshAccessToken(accessToken, scope);
				if (accessToken == null) {
					accessToken = getNewAccessToken(scope);
				}
			} else {
				accessToken = getNewAccessToken(scope);
			}
			temporaryCache.set(key,
					Math.max(accessToken.getAccessToken().getExpiresIn(),
							accessToken.getRefreshToken() == null ? 0 : accessToken.getRefreshToken().getExpiresIn()),
					accessToken.clone());
		}
		return accessToken;
	}

	public AccessToken refreshAccessToken(AccessToken accessToken, String scope) {
		return null;
	}

	protected abstract String getTemporaryCacheKey(String scope);

	public abstract AccessToken getNewAccessToken(String scope);
}
