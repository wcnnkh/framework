package io.basc.framework.convert.support;

import io.basc.framework.convert.lang.ConversionServices;
import io.basc.framework.convert.lang.ConverterConversionService;
import io.basc.framework.convert.lang.DateFormatConversionService;
import io.basc.framework.convert.lang.JsonConversionService;
import io.basc.framework.convert.lang.JsonToObjectConversionService;
import io.basc.framework.convert.lang.ObjectToStringConverter;
import io.basc.framework.convert.lang.PrimitiveConversionService;
import io.basc.framework.convert.lang.ReaderToStringConverter;
import io.basc.framework.convert.lang.ResourceToPropertiesConverter;
import io.basc.framework.convert.lang.StringToCharsetConverter;
import io.basc.framework.convert.lang.StringToCurrencyConverter;
import io.basc.framework.convert.lang.StringToLocaleConverter;
import io.basc.framework.convert.lang.StringToTimeZoneConverter;
import io.basc.framework.factory.Configurable;
import io.basc.framework.io.Resource;
import io.basc.framework.io.resolver.PropertiesResolver;
import io.basc.framework.lang.Nullable;
import io.basc.framework.value.EmptyValue;

import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Currency;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;
import java.util.function.Supplier;

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
