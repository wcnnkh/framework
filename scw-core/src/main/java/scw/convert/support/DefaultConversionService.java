package scw.convert.support;

import java.nio.charset.Charset;
import java.util.Currency;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;

import scw.convert.lang.ConversionServices;
import scw.convert.lang.ConverterConversionService;
import scw.convert.lang.DocumentParseConversionService;
import scw.convert.lang.JsonConversionService;
import scw.convert.lang.ObjectToStringConverter;
import scw.convert.lang.ResourceToPropertiesConverter;
import scw.convert.lang.StringToCharsetConverter;
import scw.convert.lang.StringToCurrencyConverter;
import scw.convert.lang.StringToLocaleConverter;
import scw.convert.lang.StringToTimeZoneConverter;
import scw.io.Resource;
import scw.io.resolver.PropertiesResolver;
import scw.lang.Nullable;
import scw.util.Supplier;

public class DefaultConversionService extends ConversionServices {

	public DefaultConversionService() {
		addConversionService(new ArrayToArrayConversionService(this));
		addConversionService(new ArrayToCollectionConversionService(this));

		addConversionService(new ByteBufferConversionService(this));

		addConversionService(new CollectionToArrayConversionService(this));
		addConversionService(new CollectionToCollectionConversionService(this));
		addConversionService(new CollectionToObjectConversionService(this));

		addConversionService(new MapToMapConversionService(this));

		addConversionService(new ValueConversionService(this));
		addConversionService(new JsonConversionService());

		addConversionService(new ConverterConversionService(String.class, Charset.class, new StringToCharsetConverter()));
		addConversionService(new ConverterConversionService(String.class, Locale.class, new StringToLocaleConverter()));
		addConversionService(new ConverterConversionService(String.class, TimeZone.class, new StringToTimeZoneConverter()));
		addConversionService(new ConverterConversionService(String.class, Currency.class, new StringToCurrencyConverter()));

		addConversionService(new EntityToMapConversionService(this));
		addConversionService(new ObjectToArrayConversionService(this));
		addConversionService(new ObjectToCollectionConversionService(this));
		addConversionService(new ObjectToStringConverter());

		// document
		addConversionService(new DocumentParseConversionService());
		addConversionService(new NodeListToCollectionConversionService(this));
		addConversionService(new NodeListToMapConversionService(this));
		addConversionService(new NodeToObjectConversionService(this));

		addConversionService(new MapToEntityConversionService(this));
		addConversionService(new PropertyFactoryToEntityConversionService(this));
		addConversionService(new NodeListToEntityConversionService(this));
	}

	public DefaultConversionService(PropertiesResolver propertiesResolver, @Nullable Supplier<Charset> charset) {
		this();
		addConversionService(new ConverterConversionService(Resource.class, Properties.class,
				new ResourceToPropertiesConverter(propertiesResolver, charset)));
	}
}
