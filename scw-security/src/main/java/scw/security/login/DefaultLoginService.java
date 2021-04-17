package scw.security.login;

import scw.core.utils.StringUtils;
import scw.data.TemporaryStorage;
import scw.data.memory.MemoryDataOperations;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

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
