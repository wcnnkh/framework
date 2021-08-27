package io.basc.framework.convert.lang;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.Converter;
import io.basc.framework.io.IOUtils;

import java.io.IOException;
import java.io.Reader;

public class ReaderToStringConverter implements Converter<Reader, String>{

	@Override
	public String convert(Reader reader) {
		try {
			return IOUtils.read(reader);
		} catch (IOException e) {
			throw new ConversionException("reader -> string", e);
		}
	}

}
