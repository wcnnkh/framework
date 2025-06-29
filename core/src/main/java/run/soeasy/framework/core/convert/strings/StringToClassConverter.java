package run.soeasy.framework.core.convert.strings;

import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ConversionFailedException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.type.ClassUtils;

@SuppressWarnings("rawtypes")
public class StringToClassConverter implements StringConverter<Class> {
	public static StringToClassConverter DEFAULT = new StringToClassConverter();

	@Override
	public Class from(String source, TypeDescriptor sourceType, TypeDescriptor targetType) throws ConversionException {
		if (StringUtils.isEmpty(source)) {
			return null;
		}

		try {
			return ClassUtils.forName(source, null);
		} catch (ClassNotFoundException | LinkageError e) {
			throw new ConversionFailedException(sourceType, targetType, source, e);
		}
	}

	@Override
	public String to(Class source, TypeDescriptor sourceType, TypeDescriptor targetType) throws ConversionException {
		return source == null ? null : source.getName();
	}

}
