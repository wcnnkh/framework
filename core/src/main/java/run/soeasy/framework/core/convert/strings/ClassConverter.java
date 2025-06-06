package run.soeasy.framework.core.convert.strings;

import run.soeasy.framework.core.DefaultClassLoaderAccessor;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ConversionFailedException;
import run.soeasy.framework.core.convert.ReversibleConverter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.type.ClassUtils;

@SuppressWarnings("rawtypes")
public class ClassConverter extends DefaultClassLoaderAccessor implements ReversibleConverter<String, Class> {

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
	public String reverseConvert(Class source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return source == null ? null : source.getName();
	}

}
