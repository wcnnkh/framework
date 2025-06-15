package run.soeasy.framework.messaging;

import java.io.IOException;
import java.io.Writer;

import lombok.NonNull;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.io.OutputSource;

public interface OutputMessage extends Message, OutputSource {

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
	default boolean isEncoded() {
		return StringUtils.isNotEmpty(getCharsetName());
	}

	@Override
	default @NonNull Pipeline<Writer, IOException> getWriterPipeline() {
		if (isEncoded()) {
			return encode(getCharsetName()).getWriterPipeline();
		}
		return OutputSource.super.getWriterPipeline();
	}

	default OutputMessage buffered() {
		return new BufferingOutputMessage<>(this);
	}
}
