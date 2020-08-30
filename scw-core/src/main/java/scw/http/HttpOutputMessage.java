package scw.http;

import scw.net.message.OutputMessage;

public interface HttpOutputMessage extends OutputMessage, HttpMessage{
	void setContentType(MediaType contentType);
}
