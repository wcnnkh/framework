package io.basc.framework.convert.lang;

import java.io.IOException;
import java.io.Reader;

import io.basc.framework.io.IOUtils;
import io.basc.framework.util.Processor;

public class ReaderToStringConverter implements Processor<Reader, String, IOException> {

	@Override
	public String process(Reader reader) throws IOException {
		return IOUtils.read(reader);
	}
}
