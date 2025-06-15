package run.soeasy.framework.messaging;

import java.io.IOException;
import java.io.Writer;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.io.OutputSourceWrapper;

@FunctionalInterface
public interface OutputMessageWrapper<W extends OutputMessage>
		extends OutputMessage, MessageWrapper<W>, OutputSourceWrapper<W> {

	@Override
	default boolean isEncoded() {
		return getSource().isEncoded();
	}

	@Override
	default @NonNull Pipeline<Writer, IOException> getWriterPipeline() {
		return getSource().getWriterPipeline();
	}

	@Override
	default void setContentType(MediaType contentType) {
		getSource().setContentType(contentType);
	}

	@Override
	default void setContentLength(long contentLength) {
		getSource().setContentLength(contentLength);
	}

	@Override
	default void setCharsetName(String charsetName) {
		getSource().setCharsetName(charsetName);
	}

	@Override
	default OutputMessage buffered() {
		return getSource().buffered();
	}
}