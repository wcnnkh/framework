package scw.mvc;

import java.util.Collection;
import java.util.Iterator;

import scw.core.utils.CollectionUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.view.HttpCode;

public final class ExceptionHandlerChain {
	private static Logger logger = LoggerFactory.getLogger(ExceptionHandlerChain.class);

	private Iterator<ExceptionHandler> iterator;

	public ExceptionHandlerChain(Collection<ExceptionHandler> collection) {
		if (!CollectionUtils.isEmpty(collection)) {
			iterator = collection.iterator();
		}
	}

	public Object doHandler(Channel channel, Throwable throwable) {
		if (iterator == null) {
			return lastHandler(channel, throwable);
		}

		if (iterator.hasNext()) {
			return iterator.next().handler(channel, throwable, this);
		} else {
			return lastHandler(channel, throwable);
		}
	}

	private Object lastHandler(Channel channel, Throwable throwable) {
		if (channel instanceof HttpChannel) {
			return httpHandler((HttpChannel) channel, throwable);
		}

		throw new RuntimeException(throwable);
	}

	private Object httpHandler(HttpChannel httpChannel, Throwable throwable) {
		logger.error(throwable, httpChannel.toString());
		return new HttpCode(500, "system error");
	}
}
