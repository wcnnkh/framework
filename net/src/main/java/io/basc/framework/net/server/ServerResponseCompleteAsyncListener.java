package io.basc.framework.net.server;

import java.io.IOException;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ServerResponseCompleteAsyncListener implements ServerAsyncListener {
	@NonNull
	private final ServerResponse response;

	@Override
	public void onComplete(ServerAsyncEvent event) throws IOException {
		response.close();
	}

	@Override
	public void onTimeout(ServerAsyncEvent event) throws IOException {
	}

	@Override
	public void onError(ServerAsyncEvent event) throws IOException {

	}

	@Override
	public void onStartAsync(ServerAsyncEvent event) throws IOException {
	}

}
