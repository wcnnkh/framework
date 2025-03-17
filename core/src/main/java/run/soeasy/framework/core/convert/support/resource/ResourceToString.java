package run.soeasy.framework.core.convert.support.resource;

import java.io.IOException;

import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.util.io.Resource;

public class ResourceToString implements Converter<Resource, String, IOException> {

	public static final ResourceToString DEFAULT = new ResourceToString();

	@Override
	public String convert(Resource source, TypeDescriptor sourceType, TypeDescriptor targetType) throws IOException {
		return source.toReaderFactory().readAllCharacters();
	}
}
