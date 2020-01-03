package scw.security.login;

import scw.core.Assert;
import scw.core.utils.ObjectUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.XUtils;
import scw.data.TemporaryCache;
import scw.security.token.SimpleUserToken;
import scw.security.token.UserToken;

public abstract class AbstractLoginService<T> implements LoginService<T> {
	private final TemporaryCache cacheService;
	private final int exp;

	public AbstractLoginService(TemporaryCache cacheService, int exp) {
		this.cacheService = cacheService;
		this.exp = exp;
	}

	protected String generatorToken(T uid) {
		return uid + XUtils.getUUID();
	}

	protected String formatUid(T uid) {
		return uid.toString();
	}

	public UserToken<T> getUserToken(String token) {
		if (StringUtils.isEmpty(token)) {
			return null;
		}

		T uid = cacheService.getAndTouch(token, exp);
		if (uid == null) {
			return null;
		}
		cacheService.touch(formatUid(uid), exp);
		return new SimpleUserToken<T>(token, uid);
	}

	public UserToken<T> getUserTokenByUid(T uid) {
		if (uid == null) {
			return null;
		}

		String token = cacheService.getAndTouch(formatUid(uid), exp);
		if (token == null) {
			return null;
		}

		cacheService.touch(token, exp);
		return new SimpleUserToken<T>(token, uid);
	}

	public boolean cancelLogin(String token) {
		if (token == null) {
			return false;
		}

		T uid = cacheService.get(token);
		if (uid != null) {
			cacheService.delete(formatUid(uid));
		}
		return cacheService.delete(token);
	}

	public boolean cancelLoginByUid(T uid) {
		if (uid == null) {
			return false;
		}

		String token = cacheService.get(formatUid(uid));
		if (token != null) {
			cacheService.delete(token);
		}
		return cacheService.delete(formatUid(uid));
	}

	public UserToken<T> login(T uid) {
		Assert.notNull(uid);

		String oldToken = cacheService.get(formatUid(uid));
		if(oldToken != null){
			cacheService.delete(oldToken);
		}
		
		String token = generatorToken(uid);
		cacheService.set(token, exp, uid);
		cacheService.set(formatUid(uid), exp, token);
		return new SimpleUserToken<T>(token, uid);
	}

	public boolean verification(String token, T uid) {
		if (uid == null || token == null) {
			return false;
		}

		UserToken<T> userToken = getUserToken(token);
		if (userToken == null) {
			return false;
		}

		return ObjectUtils.equals(uid, userToken.getUid());
	}
}
