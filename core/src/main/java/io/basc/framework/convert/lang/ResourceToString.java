package io.basc.framework.convert.lang;

import java.io.IOException;

import io.basc.framework.convert.Converter;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.annotation.CharsetName;
import io.basc.framework.io.IOUtils;
import io.basc.framework.io.Resource;

public class ResourceToString implements Converter<Resource, String, IOException> {

	public static final ResourceToString DEFAULT = new ResourceToString();

	@Override
	public String convert(Resource source, TypeDescriptor sourceType, TypeDescriptor targetType) throws IOException {
		CharsetName charsetName = targetType.getAnnotation(CharsetName.class);
		String charset = charsetName == null ? null : charsetName.value();
		return (String) ((Resource) source).read((is) -> {
			return IOUtils.toString(is, charset);
		});
	}
}
