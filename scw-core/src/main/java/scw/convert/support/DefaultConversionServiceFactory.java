package scw.convert.support;

import java.nio.charset.Charset;
import java.util.Currency;
import java.util.Locale;
import java.util.TimeZone;

import scw.json.JSONUtils;

public class DefaultConversionServiceFactory extends ConversionServiceFactory{

	public DefaultConversionServiceFactory() {
		services.add(new ArrayToArrayConversionService(this));
		services.add(new ArrayToCollectionConversionService(this));
		
		services.add(new ByteBufferConversionService(this));
		
		services.add(new CollectionToArrayConversionService(this));
		services.add(new CollectionToCollectionConversionService(this));
		services.add(new CollectionToObjectConversionService(this));
		
		services.add(new MapToMapConversionService(this));
		
		services.add(new ValueConversionService(this));
		services.add(new JsonConversionService(JSONUtils.getJsonSupport()));
		
		services.add(new ConverterConversionService(String.class, Charset.class, new StringToCharsetConverter()));
		services.add(new ConverterConversionService(String.class, Locale.class, new StringToLocaleConverter()));
		services.add(new ConverterConversionService(String.class, TimeZone.class, new StringToTimeZoneConverter()));
		services.add(new ConverterConversionService(String.class, Currency.class, new StringToCurrencyConverter()));
		
		services.add(new EntityToMapConversionService(this));
		services.add(new ConverterConversionService(Object.class,
				String.class, new ObjectToStringConverter()));
		services.add(new ObjectToArrayConversionService(this));
		services.add(new ObjectToCollectionConversionService(this));
	}
}
