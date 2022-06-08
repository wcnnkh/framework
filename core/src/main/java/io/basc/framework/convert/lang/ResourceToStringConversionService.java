package io.basc.framework.convert.lang;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ConversionFailedException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.annotation.CharsetName;
import io.basc.framework.io.IOUtils;
import io.basc.framework.io.Resource;

public class ResourceToStringConversionService extends ConditionalConversionService {

	@SuppressWarnings("unchecked")
	@Override
	public String convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		CharsetName charsetName = targetType.getAnnotation(CharsetName.class);
		String charset = charsetName == null ? null : charsetName.value();
		try {
			return (String) ((Resource) source).read((is) -> {
				return IOUtils.toString(is, charset);
			});
		} catch (IOException e) {
			throw new ConversionFailedException(sourceType, targetType, source, e);
		}
	}

	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Resource.class, String.class));
	}

}
