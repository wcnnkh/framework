package io.basc.framework.convert.strings;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ConversionFailedException;
import io.basc.framework.convert.ReversibleConverter;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.DefaultClassLoaderAccessor;
import io.basc.framework.util.StringUtils;

@SuppressWarnings("rawtypes")
public class ClassConverter extends DefaultClassLoaderAccessor
		implements ReversibleConverter<String, Class, ConversionException> {

	@Override
	public Class convert(String source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		if (StringUtils.isEmpty(source)) {
			return null;
		}

		try {
			return ClassUtils.forName(source, getClassLoader());
		} catch (ClassNotFoundException | LinkageError e) {
			throw new ConversionFailedException(sourceType, targetType, source, e);
		}
	}

	@Override
	public String invert(Class source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return source == null ? null : source.getName();
	}

}
