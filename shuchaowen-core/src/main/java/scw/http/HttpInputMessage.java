package scw.http;

import scw.net.message.InputMessage;

public interface HttpInputMessage extends InputMessage{
	HttpHeaders getHeaders();
	
	MediaType getContentType();
}
