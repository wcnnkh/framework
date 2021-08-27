package io.basc.framework.security.login;

import io.basc.framework.core.utils.StringUtils;
import io.basc.framework.data.TemporaryStorage;
import io.basc.framework.data.memory.MemoryDataOperations;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;

public class DefaultLoginService<T> extends AbstractLoginService<T> {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private static final String DEFAULT_PREFIX = "login:";
	private final String prefix;
	
	public DefaultLoginService(){
		this(new MemoryDataOperations());
		logger.info("Using memory {}", getTemporaryCache());
	}

	public DefaultLoginService(TemporaryStorage temporaryCache) {
		this(temporaryCache, 7 * 24 * 60 * 60);
	}

	public DefaultLoginService(TemporaryStorage temporaryCache, int exp) {
		this(temporaryCache, exp, DEFAULT_PREFIX);
	}

	public DefaultLoginService(TemporaryStorage temporaryCache, int exp, String prefix) {
		super(temporaryCache, exp);
		this.prefix = StringUtils.isEmpty(prefix) ? DEFAULT_PREFIX : prefix;
	}

	@Override
	protected String formatUid(T uid) {
		return prefix + uid;
	}
}
