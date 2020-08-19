package scw.http;

import scw.net.message.OutputMessage;

public interface HttpOutputMessage extends OutputMessage{
	HttpHeaders getHeaders();
	
	MediaType getContentType();
	
	void setContentType(MediaType contentType);
}
