package io.basc.framework.convert.lang;

import java.io.IOException;
import java.io.Reader;

import io.basc.framework.convert.Converter;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.io.IOUtils;

public class ReaderToString implements Converter<Reader, String, IOException> {

	public static final ReaderToString DEFAULT = new ReaderToString();

	@Override
	public String convert(Reader source, TypeDescriptor sourceType, TypeDescriptor targetType) throws IOException {
		return IOUtils.read(source);
	}

}
