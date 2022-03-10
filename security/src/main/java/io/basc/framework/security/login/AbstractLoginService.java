package io.basc.framework.security.login;

import java.util.concurrent.TimeUnit;

import io.basc.framework.data.TemporaryDataOperations;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.XUtils;

public abstract class AbstractLoginService<T> implements LoginService<T> {
	private final TemporaryDataOperations dataOperations;
	private final int exp;

	public AbstractLoginService(TemporaryDataOperations dataOperations, int exp) {
		this.dataOperations = dataOperations;
		this.exp = exp;
	}

	public TemporaryDataOperations getDataOperations() {
		return dataOperations;
	}

	public int getExp() {
		return exp;
	}

	protected String generatorToken(T uid) {
		return uid + XUtils.getUUID();
	}

	protected String formatUid(T uid) {
		return uid.toString();
	}

	@SuppressWarnings("unchecked")
	public UserToken<T> getUserToken(String token) {
		if (StringUtils.isEmpty(token)) {
			return null;
		}

		T uid = (T) dataOperations.getAndTouch(token, exp, TimeUnit.SECONDS);
		if (uid == null) {
			return null;
		}
		dataOperations.touch(formatUid(uid), exp, TimeUnit.SECONDS);
		return new UserToken<T>(token, uid);
	}

	public UserToken<T> getUserTokenByUid(T uid) {
		if (uid == null) {
			return null;
		}

		String token = dataOperations.getAndTouch(String.class, formatUid(uid), exp, TimeUnit.SECONDS);
		if (token == null) {
			return null;
		}

		dataOperations.touch(token, exp, TimeUnit.SECONDS);
		return new UserToken<T>(token, uid);
	}

	@SuppressWarnings("unchecked")
	public boolean cancelLogin(String token) {
		if (token == null) {
			return false;
		}

		T uid = (T) dataOperations.get(token);
		if (uid != null) {
			dataOperations.delete(formatUid(uid));
		}
		return dataOperations.delete(token);
	}

	public boolean cancelLoginByUid(T uid) {
		if (uid == null) {
			return false;
		}

		String token = dataOperations.get(String.class, formatUid(uid));
		if (token != null) {
			dataOperations.delete(token);
		}
		return dataOperations.delete(formatUid(uid));
	}

	public UserToken<T> login(T uid) {
		Assert.notNull(uid);

		String oldToken = dataOperations.get(String.class, formatUid(uid));
		if (oldToken != null) {
			dataOperations.delete(oldToken);
		}

		String token = generatorToken(uid);
		dataOperations.set(token, uid, exp, TimeUnit.SECONDS);
		dataOperations.set(formatUid(uid), token, exp, TimeUnit.SECONDS);
		return new UserToken<T>(token, uid);
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
