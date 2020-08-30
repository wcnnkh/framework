package scw.http;

import scw.core.utils.StringUtils;
import scw.net.message.AbstractOutputMessage;

public abstract class AbstractHttpOutputMessage extends AbstractOutputMessage implements HttpOutputMessage {
	@Override
	public MediaType getContentType() {
		String contentType = getRawContentType();
		if(StringUtils.hasText(contentType)){
			return MediaType.parseMediaType(contentType);
		}
		return null;
	}

	public void setContentType(MediaType contentType) {
		getHeaders().setContentType(contentType);
	}
}
