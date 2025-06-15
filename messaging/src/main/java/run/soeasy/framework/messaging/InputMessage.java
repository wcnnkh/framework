package run.soeasy.framework.messaging;

import java.io.IOException;
import java.io.Reader;

import lombok.NonNull;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.io.InputSource;

public interface InputMessage extends Message, InputSource {

	@Override
	default boolean isDecoded() {
		return StringUtils.isNotEmpty(getCharsetName());
	}

	@Override
	default @NonNull Pipeline<Reader, IOException> getReaderPipeline() {
		String charsetName = getCharsetName();
		if (StringUtils.isEmpty(charsetName)) {
			return decode(charsetName).getReaderPipeline();
		}
		return InputSource.super.getReaderPipeline();
	}

	default InputMessage buffered() {
		return new BufferingInputMessage<>(this);
	}
}
