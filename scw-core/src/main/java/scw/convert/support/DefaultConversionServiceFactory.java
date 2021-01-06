package scw.convert.support;

import java.nio.charset.Charset;
import java.util.Currency;
import java.util.Locale;
import java.util.TimeZone;

import scw.json.JSONUtils;

public class DefaultConversionServiceFactory extends ConversionServiceFactory{

	public DefaultConversionServiceFactory() {
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
}
