package run.soeasy.framework.core.convert.strings;

import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.convert.TypeDescriptor;

public class StringToDoubleConverter implements StringConverter<Double> {
	public static StringToDoubleConverter DEFAULT = new StringToDoubleConverter();

	@Override
	public Double from(String source, TypeDescriptor sourceType, TypeDescriptor targetType) throws NumberFormatException {
		if (StringUtils.isEmpty(source)) {
			return null;
		}
		return Double.parseDouble(source);
	}

	@Override
	public String to(Double source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws NumberFormatException {
		return source == null ? null : Double.toString(source);
	}

}
