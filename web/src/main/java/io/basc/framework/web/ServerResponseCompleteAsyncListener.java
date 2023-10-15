package io.basc.framework.web;

import java.io.IOException;

public class ServerResponseCompleteAsyncListener implements ServerAsyncListener {
	private ServerResponse response;

	public ServerResponseCompleteAsyncListener(ServerResponse response) {
		this.response = response;
	}

	@Override
	public void onComplete(ServerAsyncEvent event) throws IOException {
		response.close();
	}

	@Override
	public void onTimeout(ServerAsyncEvent event) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(ServerAsyncEvent event) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStartAsync(ServerAsyncEvent event) throws IOException {
		// TODO Auto-generated method stub

	}

}
