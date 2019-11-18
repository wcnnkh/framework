package scw.mvc.support;

import java.util.Collection;
import java.util.Iterator;

import scw.core.exception.NestedExceptionUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mvc.Channel;
import scw.mvc.ExceptionHandler;
import scw.mvc.ExceptionHandlerChain;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.view.HttpCode;

public class DefaultExceptionHandlerChain implements ExceptionHandlerChain {
	/**
	 * 是否使用父级异常，默认使用
	 */
	private static final boolean MOST_SPECIFIC_CAUSE = StringUtils
			.parseBoolean(SystemPropertyUtils.getProperty("mvc.most.specific.cause"), true);
	private static Logger logger = LoggerFactory.getLogger(ExceptionHandlerChain.class);
	private Iterator<ExceptionHandler> iterator;

	public DefaultExceptionHandlerChain(Collection<ExceptionHandler> collection) {
		if (!CollectionUtils.isEmpty(collection)) {
			iterator = collection.iterator();
		}
	}

	public Object doHandler(Channel channel, Throwable throwable) {
		Throwable error = MOST_SPECIFIC_CAUSE ? NestedExceptionUtils.getMostSpecificCause(throwable) : throwable;
		logger.error(error, channel.toString());
		if (iterator == null) {
			return lastHandler(channel, error);
		}

		if (iterator.hasNext()) {
			return iterator.next().handler(channel, error, this);
		} else {
			return lastHandler(channel, error);
		}
	}

	private Object lastHandler(Channel channel, Throwable throwable) {
		if (channel instanceof HttpChannel) {
			return httpHandler((HttpChannel) channel, throwable);
		}

		throw new RuntimeException(throwable);
	}

	private Object httpHandler(HttpChannel httpChannel, Throwable throwable) {
		return new HttpCode(500, "system error");
	}
}
