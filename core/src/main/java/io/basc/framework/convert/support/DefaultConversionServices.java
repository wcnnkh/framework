package io.basc.framework.convert.support;

import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Currency;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;

import io.basc.framework.convert.lang.ConversionServices;
import io.basc.framework.convert.lang.ConverterConversionService;
import io.basc.framework.convert.lang.DateFormatConversionService;
import io.basc.framework.convert.lang.JsonConversionService;
import io.basc.framework.convert.lang.JsonToObjectConversionService;
import io.basc.framework.convert.lang.ObjectToStringConverter;
import io.basc.framework.convert.lang.ReaderToStringConverter;
import io.basc.framework.convert.lang.ResourceToPropertiesConverter;
import io.basc.framework.convert.lang.StringToCharsetConverter;
import io.basc.framework.convert.lang.StringToCurrencyConverter;
import io.basc.framework.convert.lang.StringToLocaleConverter;
import io.basc.framework.convert.lang.StringToTimeZoneConverter;
import io.basc.framework.convert.resolve.ResourceResolverConversionService;
import io.basc.framework.convert.resolve.ResourceResolvers;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.io.Resource;

public class DefaultConversionServices extends ConversionServices {
	private final ResourceResolvers resourceResolvers;

	public DefaultConversionServices() {
		this.resourceResolvers = new ResourceResolvers(this);
		afterProperties();
	}

	public DefaultConversionServices(ResourceResolvers resourceResolvers) {
		this.resourceResolvers = resourceResolvers;
		afterProperties();
	}

	protected void afterProperties() {
		addService(new ArrayToArrayConversionService(this));
		addService(new ArrayToCollectionConversionService(this));

		addService(new ByteBufferConversionService(this));

		addService(new CollectionToArrayConversionService(this));
		addService(new CollectionToCollectionConversionService(this));
		addService(new CollectionToObjectConversionService(this));

		addService(new DateFormatConversionService());

		addService(new MapToMapConversionService(this));

		addService(new ValueConversionService(this));
		addService(new JsonConversionService());
		addService(new JsonToObjectConversionService());

		addService(
				new ConverterConversionService(String.class, Charset.class, new StringToCharsetConverter()));
		addService(new ConverterConversionService(String.class, Locale.class, new StringToLocaleConverter()));
		addService(
				new ConverterConversionService(String.class, TimeZone.class, new StringToTimeZoneConverter()));
		addService(
				new ConverterConversionService(String.class, Currency.class, new StringToCurrencyConverter()));
		addService(new ConverterConversionService(Reader.class, String.class, new ReaderToStringConverter()));

		addService(new ObjectToArrayConversionService(this));
		addService(new ObjectToCollectionConversionService(this));
		addService(new ObjectToStringConverter());

		addService(new ConverterConversionService(Resource.class, Properties.class,
				new ResourceToPropertiesConverter(resourceResolvers.getPropertiesResolvers())));
		addService(new ResourceResolverConversionService(resourceResolvers));
	}

	public ResourceResolvers getResourceResolvers() {
		return resourceResolvers;
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		resourceResolvers.configure(serviceLoaderFactory);
		super.configure(serviceLoaderFactory);
	}
}
