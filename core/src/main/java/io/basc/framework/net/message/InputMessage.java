package io.basc.framework.net.message;

import io.basc.framework.io.InputStreamSource;
import io.basc.framework.lang.Constants;

import java.io.IOException;

public interface InputMessage extends Message, InputStreamSource {

	default String getString() throws IOException {
		String charsetName = getCharacterEncoding();
		charsetName = charsetName == null ? Constants.UTF_8.name() : charsetName;
		return new String(getBytes(), charsetName);
	}
}
