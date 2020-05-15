package scw.mvc;

import java.io.IOException;

import scw.core.utils.XUtils;
import scw.net.http.server.ServerHttpAsyncEvent;
import scw.net.http.server.ServerHttpAsyncListener;
import scw.net.http.server.mvc.HttpChannel;

public class HttpChannelAsyncListener implements ServerHttpAsyncListener {
	private HttpChannel httpChannel;

	public HttpChannelAsyncListener(HttpChannel httpChannel) {
		this.httpChannel = httpChannel;
	}

	public void onComplete(ServerHttpAsyncEvent event) throws IOException {
		try {
			XUtils.destroy(httpChannel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onTimeout(ServerHttpAsyncEvent event) throws IOException {
	}

	public void onError(ServerHttpAsyncEvent event) throws IOException {
	}

	public void onStartAsync(ServerHttpAsyncEvent event) throws IOException {
	}

}
