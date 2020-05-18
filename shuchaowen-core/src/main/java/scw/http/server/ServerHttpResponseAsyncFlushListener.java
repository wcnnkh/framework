package scw.http.server;

import java.io.IOException;

public class ServerHttpResponseAsyncFlushListener implements ServerHttpAsyncListener{
	private ServerHttpResponse response;
	
	public ServerHttpResponseAsyncFlushListener(ServerHttpResponse response){
		this.response = response;
	}
	
	public void onComplete(ServerHttpAsyncEvent event) throws IOException {
		response.flush();
	}

	public void onTimeout(ServerHttpAsyncEvent event) throws IOException {
		// TODO Auto-generated method stub
		
	}

	public void onError(ServerHttpAsyncEvent event) throws IOException {
		// TODO Auto-generated method stub
		
	}

	public void onStartAsync(ServerHttpAsyncEvent event) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
