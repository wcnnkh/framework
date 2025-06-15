package run.soeasy.framework.messaging;

import java.io.IOException;
import java.io.Reader;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.io.InputSourceWrapper;

@FunctionalInterface
public interface InputMessageWrapper<W extends InputMessage>
		extends InputMessage, MessageWrapper<W>, InputSourceWrapper<W> {

	@Override
	default @NonNull Pipeline<Reader, IOException> getReaderPipeline() {
		return getSource().getReaderPipeline();
	}

	@Override
	default boolean isDecoded() {
		return getSource().isDecoded();
	}

	@Override
	default InputMessage buffered() {
		return getSource().buffered();
	}
}