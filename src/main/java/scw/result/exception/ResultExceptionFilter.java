package scw.result.exception;

import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mvc.Channel;
import scw.mvc.FilterChain;
import scw.result.Result;
import scw.transaction.TransactionManager;

public final class ResultExceptionFilter implements scw.mvc.Filter {
	private static Logger logger = LoggerFactory
			.getLogger(ResultExceptionFilter.class);
	private final ExceptionFactory exceptionFactory;

	public ResultExceptionFilter(ExceptionFactory exceptionFactory) {
		this.exceptionFactory = exceptionFactory;
	}

	public Object doFilter(Channel channel, FilterChain chain) throws Throwable {
		try {
			return chain.doFilter(channel);
		} catch (Exception e) {
			if (e instanceof ResultException) {
				logger.error(e, channel.toString());
				Result result = exceptionFactory.getResult((ResultException) e);
				if (result != null) {
					channel.write(result);
					if (result.isRollbackOnly()) {
						TransactionManager.setRollbackOnly();
					}
				}
				return null;
			} else {
				throw e;
			}
		}
	}
}
