package io.basc.framework.core.convert.strings;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.ConversionFailedException;
import io.basc.framework.core.convert.ReversibleConverter;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.io.IOUtils;

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
