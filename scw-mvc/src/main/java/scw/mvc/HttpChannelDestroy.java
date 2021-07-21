package scw.mvc;

import java.io.IOException;

import scw.context.Destroy;
import scw.context.support.LifecycleAuxiliary;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.web.ServerHttpAsyncEvent;
import scw.web.ServerHttpAsyncListener;

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
				logger.error(e, "[{}] destroy channel error: {}", MVCUtils.getRequestLogId(httpChannel.getRequest()), this.toString());
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
