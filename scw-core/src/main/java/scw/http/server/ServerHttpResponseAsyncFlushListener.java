package scw.http.server;

import java.io.IOException;

public class ServerHttpResponseAsyncFlushListener implements ServerHttpAsyncListener{
	private ServerHttpResponse response;
	
	public ServerHttpResponseAsyncFlushListener(ServerHttpResponse response){
		this.response = response;
	}
	
	public void onComplete(ServerHttpAsyncEvent event) throws IOException {
		if(!response.isCommitted()){
			response.flush();
		}
		response.close();
	}

	public void onTimeout(ServerHttpAsyncEvent event) throws IOException {
	}

	public void onError(ServerHttpAsyncEvent event) throws IOException {
	}

	public void onStartAsync(ServerHttpAsyncEvent event) throws IOException {
	}

}
