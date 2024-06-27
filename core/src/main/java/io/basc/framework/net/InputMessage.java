package io.basc.framework.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import io.basc.framework.io.InputStreamSource;
import io.basc.framework.io.ReaderSource;
import io.basc.framework.util.StringUtils;

public interface InputMessage extends Message, InputStreamSource, ReaderSource {

	@Override
	default Reader getReader() throws IOException {
		InputStream inputStream = getInputStream();
		String charsetName = getCharacterEncoding();
		return StringUtils.isEmpty(charsetName) ? new InputStreamReader(inputStream)
				: new InputStreamReader(inputStream, charsetName);
	}
}
