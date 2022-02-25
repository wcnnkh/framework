package io.basc.framework.security.login;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.data.memory.MemoryOperations;
import io.basc.framework.data.storage.TemporaryStorage;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.StringUtils;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class DefaultLoginService<T> extends AbstractLoginService<T> {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private static final String DEFAULT_PREFIX = "login:";
	private final String prefix;

	public DefaultLoginService(Class<T> uidType) {
		this(new MemoryOperations(), uidType);
		logger.info("Using memory {}", getTemporaryCache());
	}

	public DefaultLoginService(TemporaryStorage temporaryCache, Class<T> uidType) {
		this(temporaryCache, 7 * 24 * 60 * 60, uidType);
	}

	public DefaultLoginService(TemporaryStorage temporaryCache, int exp, Class<T> uidType) {
		this(temporaryCache, exp, uidType, DEFAULT_PREFIX);
	}

	public DefaultLoginService(TemporaryStorage temporaryCache, int exp, Class<T> uidType, String prefix) {
		super(temporaryCache, exp, uidType);
		this.prefix = StringUtils.isEmpty(prefix) ? DEFAULT_PREFIX : prefix;
	}

	@Override
	protected String formatUid(T uid) {
		return prefix + uid;
	}
}
