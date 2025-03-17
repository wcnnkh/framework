package run.soeasy.framework.core.convert.support.strings;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ConversionFailedException;
import run.soeasy.framework.core.convert.ReversibleConverter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.util.io.IOUtils;

public class ReaderConverter implements ReversibleConverter<String, Reader, ConversionException> {

	@Override
	public Reader convert(String source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return source == null ? null : new StringReader(source);
	}

	@Override
	public String reverseConvert(Reader source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		try {
			return IOUtils.read(source);
		} catch (IOException e) {
			throw new ConversionFailedException(sourceType, targetType, source, e);
		}
	}

}
