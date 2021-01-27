package scw.convert.support;

import java.nio.charset.Charset;
import java.util.Currency;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;
import java.util.TreeSet;

import scw.convert.ConfigurableConversionService;
import scw.convert.ConversionService;
import scw.convert.ConversionServiceAware;
import scw.convert.ConverterNotFoundException;
import scw.convert.TypeDescriptor;
import scw.io.Resource;
import scw.io.resolver.PropertiesResolver;
import scw.lang.Nullable;
import scw.util.Supplier;

public class DefaultConversionService extends
		ConvertibleConditionalComparator<Object> implements
		ConfigurableConversionService, Comparable<Object> {
	private final TreeSet<ConversionService> conversionServices = new TreeSet<ConversionService>(
			this);
	
	public DefaultConversionService() {
		conversionServices.add(new ArrayToArrayConversionService(this));
		conversionServices.add(new ArrayToCollectionConversionService(this));
		
		conversionServices.add(new ByteBufferConversionService(this));
		
		conversionServices.add(new CollectionToArrayConversionService(this));
		conversionServices.add(new CollectionToCollectionConversionService(this));
		conversionServices.add(new CollectionToObjectConversionService(this));
		
		conversionServices.add(new MapToMapConversionService(this));
		
		conversionServices.add(new ValueConversionService(this));
		conversionServices.add(new JsonConversionService());
		
		conversionServices.add(new ConverterConversionService(String.class, Charset.class, new StringToCharsetConverter()));
		conversionServices.add(new ConverterConversionService(String.class, Locale.class, new StringToLocaleConverter()));
		conversionServices.add(new ConverterConversionService(String.class, TimeZone.class, new StringToTimeZoneConverter()));
		conversionServices.add(new ConverterConversionService(String.class, Currency.class, new StringToCurrencyConverter()));
		
		conversionServices.add(new EntityToMapConversionService(this));
		conversionServices.add(new ConverterConversionService(Object.class,
				String.class, new ObjectToStringConverter()));
		conversionServices.add(new ObjectToArrayConversionService(this));
		conversionServices.add(new ObjectToCollectionConversionService(this));
		
		//document
		conversionServices.add(new DocumentParseConversionService());
		conversionServices.add(new NodeListToCollectionConversionService(this));
		conversionServices.add(new NodeListToMapConversionService(this));
		conversionServices.add(new NodeToObjectConversionService(this));

		conversionServices.add(new MapToEntityConversionService(this));
		conversionServices.add(new PropertyFactoryToEntityConversionService(this));
		conversionServices.add(new NodeListToEntityConversionService(this));
		
		conversionServices.add(new CollectionToMapConversionService(this, CollectionToMapConversionService.ANNOTATION));
	}
	
	public DefaultConversionService(PropertiesResolver propertiesResolver, @Nullable Supplier<Charset> charset){
		this();
		conversionServices.add(new ConverterConversionService(Resource.class, Properties.class, new ResourceToPropertiesConverter(propertiesResolver, charset)));
	}

	public void addConversionService(ConversionService conversionService) {
		if(conversionService instanceof ConversionServiceAware){
			((ConversionServiceAware) conversionService).setConversionService(this);
		}
		
		synchronized (conversionServices) {
			conversionServices.add(conversionService);
		}
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

	public Object convert(Object source, TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		if (sourceType != null && targetType != null
				&& targetType.isAssignableTo(sourceType)) {
			return source;
		}

		if (targetType.getType() == Object.class) {
			return source;
		}

		for (ConversionService service : conversionServices) {
			if (service.canConvert(sourceType, targetType)) {
				return service.convert(source, sourceType, targetType);
			}
		}
		throw new ConverterNotFoundException(sourceType, targetType);
	}
	
	public Object convert(Object source, TypeDescriptor targetType) {
		if(source == null){
			return null;
		}
		
		return convert(source, TypeDescriptor.forObject(source), targetType);
	}

	public int compareTo(Object o) {
		for (ConversionService service : conversionServices) {
			if (ConvertibleConditionalComparator.INSTANCE.compare(service, o) == 1) {
				return 1;
			}
		}
		return -1;
	}
}
