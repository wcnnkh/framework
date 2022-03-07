package io.basc.framework.security.login;

import java.util.concurrent.TimeUnit;

import io.basc.framework.data.TemporaryStorageOperations;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.XUtils;

public abstract class AbstractLoginService<T> implements LoginService<T> {
	private final TemporaryStorageOperations storageOperations;
	private final int exp;

	public AbstractLoginService(TemporaryStorageOperations storageOperations, int exp) {
		this.storageOperations = storageOperations;
		this.exp = exp;
	}

	public TemporaryStorageOperations getStorageOperations() {
		return storageOperations;
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

		T uid = (T) storageOperations.getAndTouch(token, exp, TimeUnit.SECONDS);
		if (uid == null) {
			return null;
		}
		storageOperations.touch(formatUid(uid), exp, TimeUnit.SECONDS);
		return new UserToken<T>(token, uid);
	}

	public UserToken<T> getUserTokenByUid(T uid) {
		if (uid == null) {
			return null;
		}

		String token = storageOperations.getAndTouch(String.class, formatUid(uid), exp, TimeUnit.SECONDS);
		if (token == null) {
			return null;
		}

		storageOperations.touch(token, exp, TimeUnit.SECONDS);
		return new UserToken<T>(token, uid);
	}

	@SuppressWarnings("unchecked")
	public boolean cancelLogin(String token) {
		if (token == null) {
			return false;
		}

		T uid = (T) storageOperations.get(token);
		if (uid != null) {
			storageOperations.delete(formatUid(uid));
		}
		return storageOperations.delete(token);
	}

	public boolean cancelLoginByUid(T uid) {
		if (uid == null) {
			return false;
		}

		String token = storageOperations.get(String.class, formatUid(uid));
		if (token != null) {
			storageOperations.delete(token);
		}
		return storageOperations.delete(formatUid(uid));
	}

	public UserToken<T> login(T uid) {
		Assert.notNull(uid);

		String oldToken = storageOperations.get(String.class, formatUid(uid));
		if (oldToken != null) {
			storageOperations.delete(oldToken);
		}

		String token = generatorToken(uid);
		storageOperations.set(token, uid, exp, TimeUnit.SECONDS);
		storageOperations.set(formatUid(uid), token, exp, TimeUnit.SECONDS);
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
