package scw.convert.lang;

import java.io.IOException;
import java.io.Reader;

import scw.convert.ConversionException;
import scw.convert.Converter;
import scw.io.IOUtils;

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
