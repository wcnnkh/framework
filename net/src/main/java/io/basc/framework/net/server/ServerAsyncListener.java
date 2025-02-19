package io.basc.framework.net.server;

import java.io.IOException;

public interface ServerAsyncListener {
	void onComplete(ServerAsyncEvent event) throws IOException;

	void onTimeout(ServerAsyncEvent event) throws IOException;

	void onError(ServerAsyncEvent event) throws IOException;

	void onStartAsync(ServerAsyncEvent event) throws IOException;
}
