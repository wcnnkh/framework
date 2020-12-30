package scw.convert.support;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;

import scw.convert.ConversionService;
import scw.convert.ConverterNotFoundException;
import scw.convert.TypeDescriptor;
import scw.json.JSONUtils;

public class ConversionServiceFactory implements ConversionService,
		Comparator<Object>, Comparable<Object> {
	private final TreeSet<ConversionService> conversionServices = new TreeSet<ConversionService>(
			this);

	public ConversionServiceFactory() {
		conversionServices.add(new ArrayToArrayConversionService(this));
		conversionServices.add(new ArrayToCollectionConversionService(this));
		
		conversionServices.add(new ByteBufferConversionService(this));
		
		conversionServices.add(new CollectionToArrayConversionService(this));
		conversionServices.add(new CollectionToCollectionConversionService(this));
		conversionServices.add(new CollectionToObjectConversionService(this));
		
		conversionServices.add(new MapToMapConversionService(this));
		
		conversionServices.add(new ValueConversionService(this));
		conversionServices.add(new JsonConversionService(JSONUtils.getJsonSupport()));
		
		conversionServices.add(new ConverterConversionService(String.class, Charset.class, new StringToCharsetConverter()));
		conversionServices.add(new ConverterConversionService(String.class, Locale.class, new StringToLocaleConverter()));
		conversionServices.add(new ConverterConversionService(String.class, TimeZone.class, new StringToTimeZoneConverter()));
		conversionServices.add(new ConverterConversionService(String.class, Currency.class, new StringToCurrencyConverter()));
		
		conversionServices.add(new EntityToMapConversionService(this));
		conversionServices.add(new ConverterConversionService(Object.class,
				String.class, new ObjectToStringConverter()));
		conversionServices.add(new ObjectToArrayConversionService(this));
		conversionServices.add(new ObjectToCollectionConversionService(this));
	}

	public ConversionService getConversionService(TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		for (ConversionService service : conversionServices) {
			if (service.canConvert(sourceType, targetType)) {
				return service;
			}
		}
		return null;
	}

	public SortedSet<ConversionService> getConversionServices() {
		return Collections.synchronizedSortedSet(conversionServices);
	}

	public boolean canConvert(TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		for (ConversionService service : conversionServices) {
			if (service.canConvert(sourceType, targetType)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public final <T> T convert(Object source, Type type) {
		return (T) convert(source,
				source == null ? null : TypeDescriptor.forObject(source),
				TypeDescriptor.valueOf(type));
	}

	public final Object convert(Object source, TypeDescriptor targetType) {
		return convert(source,
				source == null ? null : TypeDescriptor.forObject(source),
				targetType);
	}

	public Object convert(Object source, TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		for (ConversionService service : conversionServices) {
			if (service.canConvert(sourceType, targetType)) {
				return service.convert(source, sourceType, targetType);
			}
		}
		throw new ConverterNotFoundException(sourceType, targetType);
	}

	public int compareTo(Object o) {
		for (ConversionService service : conversionServices) {
			if (ConvertibleConditionalComparator.INSTANCE.compare(service, o) == 1) {
				return 1;
			}
		}
		return -1;
	}

	public int compare(Object o1, Object o2) {
		return ConvertibleConditionalComparator.INSTANCE.compare(o1, o2) == 1 ? 1
				: -1;
	}
}
