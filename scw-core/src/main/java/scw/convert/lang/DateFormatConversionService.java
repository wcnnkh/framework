package scw.convert.lang;

import java.util.Date;

import scw.convert.ConversionException;
import scw.convert.ConversionFailedException;
import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.convert.annotation.DateFormat;
import scw.core.utils.NumberUtils;
import scw.util.FormatUtils;
import scw.value.AnyValue;

public class DateFormatConversionService implements ConversionService {
	private boolean canConvert(Class<?> type) {
		return type == String.class || type == Date.class
				|| NumberUtils.isNumber(type);
	}

	@Override
	public boolean canConvert(TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		return canConvert(sourceType.getType())
				&& canConvert(targetType.getType());
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType,
			TypeDescriptor targetType) throws ConversionException {
		if (source == null || sourceType == null) {
			return null;
		}

		if (canDirectlyConvert(sourceType, targetType)) {
			return source;
		}

		if (sourceType.getType() == String.class) {
			if (targetType.getType() == Date.class) {
				return stringToDate((String) source, sourceType, targetType);
			}

			if (NumberUtils.isNumber(targetType.getType())) {
				return stringToNumber((String) source, sourceType, targetType);
			}
		}

		if (sourceType.getType() == Date.class) {
			if (targetType.getType() == String.class) {
				return dateToString((Date) source, sourceType, targetType);
			}

			if (NumberUtils.isNumber(targetType.getType())) {
				return dateToNumber((Date) source, sourceType, targetType);
			}
		}

		if (NumberUtils.isNumber(sourceType.getType())) {
			if(targetType.getType() == String.class){
				return numberToString(source, sourceType, targetType);
			}
			
			if(targetType.getType() == Date.class){
				return numberToString(source, sourceType, targetType);
			}
			
			if(NumberUtils.isNumber(targetType.getType())){
				return new AnyValue(source).getAsObject(targetType.getResolvableType());
			}
		}
		
		throw new ConversionFailedException(sourceType, targetType, source, null);
	}

	private Date stringToDate(String source, TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		DateFormat dateFormat = sourceType.getAnnotation(DateFormat.class);
		if (dateFormat == null) {
			return FormatUtils.getDate(source, "");
		} else {
			return FormatUtils.getDate(source, dateFormat.value());
		}
	}

	private Object stringToNumber(String source, TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		Date date = stringToDate(source, sourceType,
				targetType.narrow(Date.class));
		return dateToNumber(date, sourceType.narrow(date), targetType);
	}

	private String dateToString(Date source, TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		DateFormat dateFormat = targetType.getAnnotation(DateFormat.class);
		if (dateFormat == null) {
			return String.valueOf(source);
		} else {
			return FormatUtils.dateFormat(source, dateFormat.value());
		}
	}

	private Object dateToNumber(Date source, TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		return new AnyValue(source.getTime()).getAsObject(targetType
				.getResolvableType());
	}
	
	private Date numberToDate(Object source, TypeDescriptor sourceType, TypeDescriptor targetType){
		AnyValue anyValue = new AnyValue(source);
		long time = anyValue.getAsLongValue();
		return new Date(time);
	}
	
	private String numberToString(Object source, TypeDescriptor sourceType, TypeDescriptor targetType){
		Date date = numberToDate(source, sourceType, targetType);
		DateFormat dateFormat = targetType.getAnnotation(DateFormat.class);
		if (dateFormat == null) {
			return String.valueOf(date);
		} else {
			return FormatUtils.dateFormat(date, dateFormat.value());
		}
	}
}
