package run.soeasy.framework.messaging;

import java.io.InputStream;
import java.io.Reader;

import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.io.InputFactory;
import run.soeasy.framework.io.InputStreamSource;

public interface InputMessage extends Message, InputStreamSource<InputStream> {

	@Override
	default InputFactory<InputStream, Reader> decode() {
		String charsetName = getCharsetName();
		if (StringUtils.isEmpty(charsetName)) {
			return InputStreamSource.super.decode();
		}
		return decode(charsetName);
	}

	default InputMessage buffered() {
		return new BufferingInputMessage<>(this);
	}
}
