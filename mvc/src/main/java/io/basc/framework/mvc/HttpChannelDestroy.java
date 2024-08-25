package io.basc.framework.mvc;

import java.io.IOException;

import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LoggerFactory;
import io.basc.framework.web.ServerHttpAsyncEvent;
import io.basc.framework.web.ServerHttpAsyncListener;
import io.basc.framework.web.WebUtils;

public class HttpChannelDestroy implements ServerHttpAsyncListener {
	private static Logger logger = LoggerFactory.getLogger(HttpChannelDestroy.class);
	private final HttpChannel httpChannel;

	public HttpChannelDestroy(HttpChannel httpChannel) {
		this.httpChannel = httpChannel;
	}

	public void destroy() throws IOException {
		if (!httpChannel.isCompleted()) {
			try {
				if (httpChannel instanceof io.basc.framework.beans.Destroy) {
					((io.basc.framework.beans.Destroy) httpChannel).destroy();
				}
			} catch (Throwable e) {
				logger.error(e, "[{}] destroy channel error: {}",
						WebUtils.getMessageId(httpChannel.getRequest(), httpChannel.getResponse()), this.toString());
			}
		}
		httpChannel.getResponse().close();
	}

	public void onComplete(ServerHttpAsyncEvent event) throws IOException {
		destroy();
	}

	public void onTimeout(ServerHttpAsyncEvent event) throws IOException {
		// ignore
	}

	public void onError(ServerHttpAsyncEvent event) throws IOException {
		// ignore
	}

	public void onStartAsync(ServerHttpAsyncEvent event) throws IOException {
		// ignore
	}

	@Override
	public String toString() {
		return httpChannel.toString();
	}
}
