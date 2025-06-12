package run.soeasy.framework.messaging;

import java.io.OutputStream;
import java.io.Writer;

import run.soeasy.framework.io.pipeline.OutputFactory;
import run.soeasy.framework.io.pipeline.OutputStreamSourceWrapper;

@FunctionalInterface
public interface OutputMessageWrapper<W extends OutputMessage>
		extends OutputMessage, MessageWrapper<W>, OutputStreamSourceWrapper<OutputStream, W> {

	@Override
	default OutputFactory<OutputStream, Writer> encode() {
		return getSource().encode();
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