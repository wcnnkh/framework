package scw.net.message;

import java.io.IOException;

import scw.core.Constants;
import scw.io.InputStreamSource;

public interface InputMessage extends Message, InputStreamSource {

	default String getString() throws IOException {
		String charsetName = getCharacterEncoding();
		charsetName = charsetName == null ? Constants.UTF_8.name() : charsetName;
		return new String(getBytes(), charsetName);
	}
}
