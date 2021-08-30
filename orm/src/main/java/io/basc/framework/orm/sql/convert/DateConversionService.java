package io.basc.framework.orm.sql.convert;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.ConditionalConversionService;
import io.basc.framework.convert.lang.ConvertiblePair;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

public class DateConversionService extends ConditionalConversionService {

	@Override
	public Object convert(Object source, TypeDescriptor sourceType,
			TypeDescriptor targetType) throws ConversionException {
		if(targetType.getType() == Timestamp.class){
			return new Timestamp(((Date) source).getTime());
		}else if(targetType.getType() == Time.class){
			return new Time(((Date) source).getTime());
		}
		return new java.sql.Date(((Date) source).getTime());
	}

	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Date.class,
				java.sql.Date.class));
	}

}
