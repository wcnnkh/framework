package scw.http;

import java.io.IOException;
import java.io.InputStream;

import scw.net.message.InputMessage;

public class DefaultHttpInputMessage implements HttpInputMessage{
	private HttpHeaders httpHeaders = new HttpHeaders();
	private InputMessage inputMessage;
	
	public DefaultHttpInputMessage(InputMessage inputMessage){
		this.inputMessage = inputMessage;
		this.httpHeaders.putAll(inputMessage.getHeaders());
	}
	
	@Override
	public InputStream getInputStream() throws IOException {
		return inputMessage.getInputStream();
	}
	
	public HttpHeaders getHeaders() {
		return httpHeaders;
	}
}
