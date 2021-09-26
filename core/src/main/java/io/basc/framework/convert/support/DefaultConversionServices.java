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
		addConversionService(new ArrayToArrayConversionService(this));
		addConversionService(new ArrayToCollectionConversionService(this));

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

		addConversionService(new ObjectToArrayConversionService(this));
		addConversionService(new ObjectToCollectionConversionService(this));
		addConversionService(new ObjectToStringConverter());

		addConversionService(new ConverterConversionService(Resource.class, Properties.class,
				new ResourceToPropertiesConverter(resourceResolvers.getPropertiesResolvers())));
		addConversionService(new ResourceResolverConversionService(resourceResolvers));
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
