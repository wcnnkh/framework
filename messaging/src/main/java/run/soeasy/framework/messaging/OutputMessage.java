package run.soeasy.framework.messaging;

import java.io.OutputStream;
import java.io.Writer;

import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.io.pipeline.OutputFactory;
import run.soeasy.framework.core.io.pipeline.OutputStreamSource;

public interface OutputMessage extends Message, OutputStreamSource<OutputStream> {

	default void setContentType(MediaType contentType) {
		String charsetName = contentType.getCharsetName();
		if (charsetName == null) {
			charsetName = getCharsetName();
			if (charsetName == null) {
				getHeaders().setContentType(contentType);
			} else {
				getHeaders().setContentType(new MediaType(contentType, charsetName));
			}
		} else {
			getHeaders().setContentType(contentType);
		}
	}

	default void setContentLength(long contentLength) {
		getHeaders().setContentLength(contentLength);
	}

	default void setCharsetName(String charsetName) {
		MediaType mediaType = getContentType();
		if (mediaType == null) {
			mediaType = MediaType.ALL;
			return;
		}

		setContentType(new MediaType(mediaType, charsetName));
	}

	@Override
	default OutputFactory<OutputStream, Writer> encode() {
		String charsetName = getCharsetName();
		if (StringUtils.isEmpty(charsetName)) {
			return OutputStreamSource.super.encode();
		}
		return encode(charsetName);
	}

	default OutputMessage buffered() {
		return new BufferingOutputMessage<>(this);
	}
}
