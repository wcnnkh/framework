package scw.security.login;

import scw.core.utils.StringUtils;
import scw.core.utils.XUtils;
import scw.data.cache.CacheService;
import scw.security.token.SimpleUserToken;
import scw.security.token.UserToken;

public abstract class AbstractLoginFactory<T> implements LoginFactory<T> {
	private final CacheService cacheService;
	private final int exp;

	public AbstractLoginFactory(CacheService cacheService, int exp) {
		this.cacheService = cacheService;
		this.exp = exp;
	}

	protected String generatorToken(String prefix) {
		return prefix + XUtils.getUUID();
	}

	protected String formatKey(Object key) {
		return key.toString();
	}

	public UserToken<T> getUserToken(String token) {
		if (StringUtils.isNull(token)) {
			return null;
		}

		T uid = cacheService.getAndTouch(formatKey(token), exp);
		if (uid == null) {
			return null;
		}
		cacheService.touch(formatKey(uid), exp);
		return new SimpleUserToken<T>(token, uid);
	}

	public UserToken<T> getUserTokenByUid(T uid) {
		if (uid == null) {
			return null;
		}

		String token = cacheService.getAndTouch(formatKey(uid), exp);
		if (token == null) {
			return null;
		}

		cacheService.touch(formatKey(token), exp);
		return new SimpleUserToken<T>(token, uid);
	}

	public void cancelLogin(String token) {
		if (token == null) {
			return;
		}

		T uid = cacheService.get(formatKey(token));
		if (uid != null) {
			cacheService.delete(formatKey(uid));
		}
		cacheService.delete(formatKey(token));
	}

	public void cancelLoginByUid(T uid) {
		if (uid == null) {
			return;
		}

		String token = cacheService.get(formatKey(uid));
		if (token != null) {
			cacheService.delete(formatKey(token));
		}
		cacheService.delete(formatKey(uid));
	}

	public UserToken<T> login(T uid) {
		return login(generatorToken(uid.toString()), uid);
	}

	public UserToken<T> login(String sessionId, T uid) {
		cacheService.set(formatKey(sessionId), exp, uid);
		cacheService.set(formatKey(uid), exp, sessionId);
		return new SimpleUserToken<T>(sessionId, uid);
	}
}
