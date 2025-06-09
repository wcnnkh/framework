package run.soeasy.framework.messaging;

import java.io.InputStream;
import java.io.Reader;

import run.soeasy.framework.core.io.pipeline.InputFactory;
import run.soeasy.framework.core.io.pipeline.InputStreamSourceWrapper;

@FunctionalInterface
public interface InputMessageWrapper<W extends InputMessage>
		extends InputMessage, MessageWrapper<W>, InputStreamSourceWrapper<InputStream, W> {
	@Override
	default InputFactory<InputStream, Reader> decode() {
		return getSource().decode();
	}

	@Override
	default InputMessage buffered() {
		return getSource().buffered();
	}
}