package scw.http;

import scw.net.message.AbstractInputMessage;

public abstract class AbstractHttpInputMessage extends AbstractInputMessage implements HttpInputMessage{
	
	public MediaType getContentType() {
		return getHeaders().getContentType();
	};
}
