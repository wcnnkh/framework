package scw.http;

import scw.net.message.AbstractOutputMessage;

public abstract class AbstractHttpOutputMessage extends AbstractOutputMessage implements HttpOutputMessage {
	@Override
	public MediaType getContentType() {
		return getHeaders().getContentType();
	}

	public void setContentType(MediaType contentType) {
		getHeaders().setContentType(contentType);
	}
}
