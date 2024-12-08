package io.basc.framework.core.convert.lang;

import java.io.IOException;

import io.basc.framework.core.convert.Converter;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.util.io.Resource;

public class ResourceToString implements Converter<Resource, String, IOException> {

	public static final ResourceToString DEFAULT = new ResourceToString();

	@Override
	public String convert(Resource source, TypeDescriptor sourceType, TypeDescriptor targetType) throws IOException {
		return source.toReaderFactory().readAllCharacters();
	}
}
