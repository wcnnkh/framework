package io.basc.framework.security.login;

import io.basc.framework.context.annotation.ConditionalOnParameters;
import io.basc.framework.data.TemporaryDataOperations;
import io.basc.framework.data.memory.MemoryOperations;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.comparator.Ordered;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LogManager;

@ConditionalOnParameters(order = Ordered.LOWEST_PRECEDENCE)
public class DefaultLoginService<T> extends AbstractLoginService<T> {
	private Logger logger = LogManager.getLogger(getClass());
	private static final String DEFAULT_PREFIX = "login:";
	private final String prefix;

	public DefaultLoginService() {
		this(new MemoryOperations());
		logger.info("Using memory {}", getDataOperations());
	}

	public DefaultLoginService(TemporaryDataOperations dataOperations) {
		this(dataOperations, 7 * 24 * 60 * 60);
	}

	public DefaultLoginService(TemporaryDataOperations dataOperations, int exp) {
		this(dataOperations, exp, DEFAULT_PREFIX);
	}

	public DefaultLoginService(TemporaryDataOperations dataOperations, int exp, String prefix) {
		super(dataOperations, exp);
		this.prefix = StringUtils.isEmpty(prefix) ? DEFAULT_PREFIX : prefix;
	}

	@Override
	protected String formatUid(T uid) {
		return prefix + uid;
	}
}
