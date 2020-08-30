package scw.http;

import scw.net.message.Message;

public interface HttpMessage extends Message{
	HttpHeaders getHeaders();
	
	MediaType getContentType();
}
