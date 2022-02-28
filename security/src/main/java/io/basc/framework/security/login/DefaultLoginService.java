package io.basc.framework.security.login;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.data.TemporaryStorageOperations;
import io.basc.framework.data.memory.MemoryOperations;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.StringUtils;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class DefaultLoginService<T> extends AbstractLoginService<T> {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private static final String DEFAULT_PREFIX = "login:";
	private final String prefix;

	public DefaultLoginService() {
		this(new MemoryOperations());
		logger.info("Using memory {}", getStorageOperations());
	}

	public DefaultLoginService(TemporaryStorageOperations storageOperations) {
		this(storageOperations, 7 * 24 * 60 * 60);
	}

	public DefaultLoginService(TemporaryStorageOperations storageOperations, int exp) {
		this(storageOperations, exp, DEFAULT_PREFIX);
	}

	public DefaultLoginService(TemporaryStorageOperations storageOperations, int exp, String prefix) {
		super(storageOperations, exp);
		this.prefix = StringUtils.isEmpty(prefix) ? DEFAULT_PREFIX : prefix;
	}

	@Override
	protected String formatUid(T uid) {
		return prefix + uid;
	}
}
