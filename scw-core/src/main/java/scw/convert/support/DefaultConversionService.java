package scw.convert.support;

import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Currency;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;
import java.util.function.Supplier;

import scw.convert.lang.ConversionServices;
import scw.convert.lang.ConverterConversionService;
import scw.convert.lang.DateFormatConversionService;
import scw.convert.lang.JsonConversionService;
import scw.convert.lang.JsonToObjectConversionService;
import scw.convert.lang.ObjectToStringConverter;
import scw.convert.lang.PrimitiveConversionService;
import scw.convert.lang.ReaderToStringConverter;
import scw.convert.lang.ResourceToPropertiesConverter;
import scw.convert.lang.StringToCharsetConverter;
import scw.convert.lang.StringToCurrencyConverter;
import scw.convert.lang.StringToLocaleConverter;
import scw.convert.lang.StringToTimeZoneConverter;
import scw.instance.Configurable;
import scw.io.Resource;
import scw.io.resolver.PropertiesResolver;
import scw.lang.Nullable;
import scw.value.EmptyValue;

public class DefaultConversionService extends ConversionServices implements Configurable {

	public DefaultConversionService() {
		addConversionService(new ArrayToArrayConversionService(this));
		addConversionService(new ArrayToCollectionConversionService(this));
		addConversionService(new PrimitiveConversionService(EmptyValue.INSTANCE));

		addConversionService(new ByteBufferConversionService(this));

		addConversionService(new CollectionToArrayConversionService(this));
		addConversionService(new CollectionToCollectionConversionService(this));
		addConversionService(new CollectionToObjectConversionService(this));

		addConversionService(new DateFormatConversionService());

		addConversionService(new MapToMapConversionService(this));

		addConversionService(new ValueConversionService(this));
		addConversionService(new JsonConversionService());
		addConversionService(new JsonToObjectConversionService());

		addConversionService(
				new ConverterConversionService(String.class, Charset.class, new StringToCharsetConverter()));
		addConversionService(new ConverterConversionService(String.class, Locale.class, new StringToLocaleConverter()));
		addConversionService(
				new ConverterConversionService(String.class, TimeZone.class, new StringToTimeZoneConverter()));
		addConversionService(
				new ConverterConversionService(String.class, Currency.class, new StringToCurrencyConverter()));
		addConversionService(new ConverterConversionService(Reader.class, String.class, new ReaderToStringConverter()));

		addConversionService(new EntityToMapConversionService(this));
		addConversionService(new ObjectToArrayConversionService(this));
		addConversionService(new ObjectToCollectionConversionService(this));
		addConversionService(new ObjectToStringConverter());
	}

	public DefaultConversionService(PropertiesResolver propertiesResolver, @Nullable Supplier<Charset> charset) {
		this();
		addConversionService(new ConverterConversionService(Resource.class, Properties.class,
				new ResourceToPropertiesConverter(propertiesResolver, charset)));
	}
}
