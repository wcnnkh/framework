package io.basc.framework.oauth2.client;

import java.util.concurrent.TimeUnit;

import io.basc.framework.data.TemporaryStorageOperations;
import io.basc.framework.oauth2.AccessToken;

public abstract class AbstractCredentialsClient implements CredentialsClient {
	protected final TemporaryStorageOperations storageOperations;
	private int tokenExpireAheadTime = 60;// token提前过期时间

	public AbstractCredentialsClient(TemporaryStorageOperations storageOperations) {
		this.storageOperations = storageOperations;
	}

	public final int getTokenExpireAheadTime() {
		return tokenExpireAheadTime;
	}

	public void setTokenExpireAheadTime(int tokenExpireAheadTime) {
		this.tokenExpireAheadTime = tokenExpireAheadTime;
	}

	public AccessToken getAccessToken(String scope) {
		if (storageOperations == null) {
			return getNewAccessToken(scope);
		}

		String key = getTemporaryCacheKey(scope);
		AccessToken accessToken = storageOperations.get(AccessToken.class, key);
		if (accessToken == null || accessToken.getToken().isExpired(tokenExpireAheadTime)) {
			if (accessToken != null && accessToken.getRefreshToken() != null
					&& !accessToken.getRefreshToken().isExpired(tokenExpireAheadTime)) {
				accessToken = refreshAccessToken(accessToken, scope);
				if (accessToken == null) {
					accessToken = getNewAccessToken(scope);
				}
			} else {
				accessToken = getNewAccessToken(scope);
			}
			storageOperations.set(key, accessToken.clone(),
					Math.max(accessToken.getToken().getExpiresIn(),
							accessToken.getRefreshToken() == null ? 0 : accessToken.getRefreshToken().getExpiresIn()),
					TimeUnit.SECONDS);
		}
		return accessToken;
	}

	public AccessToken refreshAccessToken(AccessToken accessToken, String scope) {
		return null;
	}

	protected abstract String getTemporaryCacheKey(String scope);

	public abstract AccessToken getNewAccessToken(String scope);
}
