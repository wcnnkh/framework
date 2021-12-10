package io.basc.framework.mvc;

import java.io.IOException;

import io.basc.framework.context.Destroy;
import io.basc.framework.context.support.LifecycleAuxiliary;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.web.ServerHttpAsyncEvent;
import io.basc.framework.web.ServerHttpAsyncListener;
import io.basc.framework.web.WebUtils;

public class HttpChannelDestroy implements Destroy, ServerHttpAsyncListener {
	private static Logger logger = LoggerFactory.getLogger(HttpChannelDestroy.class);
	private final HttpChannel httpChannel;

	public HttpChannelDestroy(HttpChannel httpChannel) {
		this.httpChannel = httpChannel;
	}

	public void destroy() throws IOException {
		if (!httpChannel.isCompleted()) {
			try {
				LifecycleAuxiliary.destroy(httpChannel);
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
