package run.soeasy.framework.core.convert.support.strings;

import java.io.IOException;
import java.io.Reader;

import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.util.io.IOUtils;

public class ReaderToString implements Converter<Reader, String, IOException> {

	public static final ReaderToString DEFAULT = new ReaderToString();

	@Override
	public String convert(Reader source, TypeDescriptor sourceType, TypeDescriptor targetType) throws IOException {
		return IOUtils.read(source);
	}

}
