package scw.mvc;

import java.io.IOException;

import scw.beans.BeanUtils;
import scw.http.server.ServerHttpAsyncEvent;
import scw.http.server.ServerHttpAsyncListener;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public class HttpChannelAsyncListener implements ServerHttpAsyncListener {
	private static Logger logger = LoggerUtils.getLogger(HttpChannelAsyncListener.class);
	private HttpChannel httpChannel;

	public HttpChannelAsyncListener(HttpChannel httpChannel) {
		this.httpChannel = httpChannel;
	}

	public void onComplete(ServerHttpAsyncEvent event) throws IOException {
		if(!httpChannel.isCompleted()){
			try {
				BeanUtils.destroy(httpChannel);
			} catch (Exception e) {
				logger.error(e, "destroy channel error: " + httpChannel.toString());
			}
		}
		httpChannel.getResponse().close();
	}

	public void onTimeout(ServerHttpAsyncEvent event) throws IOException {
	}

	public void onError(ServerHttpAsyncEvent event) throws IOException {
	}

	public void onStartAsync(ServerHttpAsyncEvent event) throws IOException {
	}

}
