package scw.security.login;

import scw.core.utils.StringUtils;
import scw.data.TemporaryCache;

public class DefaultLoginService<T> extends AbstractLoginService<T> {
	private static final String DEFAULT_PREFIX = "login:";
	private final String prefix;

	public DefaultLoginService(TemporaryCache temporaryCache) {
		this(temporaryCache, 7 * 24 * 60 * 60);
	}

	public DefaultLoginService(TemporaryCache temporaryCache, int exp) {
		this(temporaryCache, exp, DEFAULT_PREFIX);
	}

	public DefaultLoginService(TemporaryCache temporaryCache, int exp, String prefix) {
		super(temporaryCache, exp);
		this.prefix = StringUtils.isEmpty(prefix) ? DEFAULT_PREFIX : prefix;
	}

	@Override
	protected String formatUid(T uid) {
		return prefix + uid;
	}
}
