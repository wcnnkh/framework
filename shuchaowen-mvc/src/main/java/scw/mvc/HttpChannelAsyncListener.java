package scw.mvc;

import java.io.IOException;

import scw.beans.BeanUtils;
import scw.http.server.ServerHttpAsyncEvent;
import scw.http.server.ServerHttpAsyncListener;

public class HttpChannelAsyncListener implements ServerHttpAsyncListener {
	private HttpChannel httpChannel;

	public HttpChannelAsyncListener(HttpChannel httpChannel) {
		this.httpChannel = httpChannel;
	}

	public void onComplete(ServerHttpAsyncEvent event) throws IOException {
		if(!httpChannel.isCompleted()){
			try {
				BeanUtils.destroy(httpChannel);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void onTimeout(ServerHttpAsyncEvent event) throws IOException {
	}

	public void onError(ServerHttpAsyncEvent event) throws IOException {
	}

	public void onStartAsync(ServerHttpAsyncEvent event) throws IOException {
	}

}
