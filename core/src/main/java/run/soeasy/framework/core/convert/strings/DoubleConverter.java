package run.soeasy.framework.core.convert.strings;

import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.service.ReversibleConverter;

public class DoubleConverter implements ReversibleConverter<String, Double> {

	@Override
	public Double convert(String source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws NumberFormatException {
		if (StringUtils.isEmpty(source)) {
			return targetType.isPrimitive() ? (double) 0 : null;
		}
		return Double.parseDouble(source);
	}

	@Override
	public String reverseConvert(Double source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws NumberFormatException {
		return source == null ? null : source.toString();
	}

}
