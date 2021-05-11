package scw.orm.convert;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

import scw.convert.ConversionException;
import scw.convert.TypeDescriptor;
import scw.convert.lang.ConditionalConversionService;
import scw.convert.lang.ConvertiblePair;

public class DateConversionService extends ConditionalConversionService {

	@Override
	public Object convert(Object source, TypeDescriptor sourceType,
			TypeDescriptor targetType) throws ConversionException {
		return new java.sql.Date(((Date) source).getTime());
	}

	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Date.class,
				java.sql.Date.class));
	}

}
