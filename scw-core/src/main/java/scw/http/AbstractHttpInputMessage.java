package scw.http;

import scw.core.utils.StringUtils;
import scw.net.message.AbstractInputMessage;

public abstract class AbstractHttpInputMessage extends AbstractInputMessage implements HttpInputMessage{
	
	public MediaType getContentType() {
		String contentType = getRawContentType();
		return StringUtils.hasText(contentType)? MediaType.parseMediaType(contentType) : null;
	};
}
