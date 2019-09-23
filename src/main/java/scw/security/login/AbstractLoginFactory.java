package scw.security.login;

import scw.core.utils.StringUtils;
import scw.core.utils.XUtils;
import scw.data.cache.TemporaryCache;
import scw.security.token.SimpleUserToken;
import scw.security.token.UserToken;

public abstract class AbstractLoginFactory<T> implements LoginFactory<T> {
	private final TemporaryCache temporaryCache;
	private final int exp;

	public AbstractLoginFactory(TemporaryCache temporaryCache, int exp) {
		this.temporaryCache = temporaryCache;
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

		T uid = temporaryCache.getAndTouch(formatKey(token), exp);
		if (uid == null) {
			return null;
		}
		temporaryCache.touch(formatKey(uid), exp);
		return new SimpleUserToken<T>(token, uid);
	}

	public UserToken<T> getUserTokenByUid(T uid) {
		if (uid == null) {
			return null;
		}

		String token = temporaryCache.getAndTouch(formatKey(uid), exp);
		if (token == null) {
			return null;
		}

		temporaryCache.touch(formatKey(token), exp);
		return new SimpleUserToken<T>(token, uid);
	}

	public void cancelLogin(String token) {
		if (token == null) {
			return;
		}

		T uid = temporaryCache.get(formatKey(token));
		if (uid != null) {
			temporaryCache.delete(formatKey(uid));
		}
		temporaryCache.delete(formatKey(token));
	}

	public void cancelLoginByUid(T uid) {
		if (uid == null) {
			return;
		}

		String token = temporaryCache.get(formatKey(uid));
		if (token != null) {
			temporaryCache.delete(formatKey(token));
		}
		temporaryCache.delete(formatKey(uid));
	}

	public UserToken<T> login(T uid) {
		return login(generatorToken(uid.toString()), uid);
	}

	public UserToken<T> login(String sessionId, T uid) {
		temporaryCache.set(formatKey(sessionId), exp, uid);
		temporaryCache.set(formatKey(uid), exp, sessionId);
		return new SimpleUserToken<T>(sessionId, uid);
	}
}
