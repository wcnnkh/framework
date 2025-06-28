package run.soeasy.framework.core.convert.support;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ConversionService;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.number.NumberToEnumConverter;
import run.soeasy.framework.core.convert.strings.StringToBigDecimalConverter;
import run.soeasy.framework.core.convert.strings.StringToBigIntegerConverter;
import run.soeasy.framework.core.convert.strings.StringToBooleanConverter;
import run.soeasy.framework.core.convert.strings.StringToByteConverter;
import run.soeasy.framework.core.convert.strings.StringToCharacterConverter;
import run.soeasy.framework.core.convert.strings.StringToCharsetConverter;
import run.soeasy.framework.core.convert.strings.StringToClassConverter;
import run.soeasy.framework.core.convert.strings.StringToCurrencyConverter;
import run.soeasy.framework.core.convert.strings.StringToDoubleConverter;
import run.soeasy.framework.core.convert.strings.StringToEnumConverter;
import run.soeasy.framework.core.convert.strings.StringToFloatConverter;
import run.soeasy.framework.core.convert.strings.StringToIntegerConverter;
import run.soeasy.framework.core.convert.strings.StringToLocaleConverter;
import run.soeasy.framework.core.convert.strings.StringToLongConverter;
import run.soeasy.framework.core.convert.strings.StringToShortConverter;
import run.soeasy.framework.core.convert.strings.StringToTimeZoneConverter;

/**
 * 全局的ConversionService
 * 
 * @author soeasy.run
 *
 */
public class SystemConversionService extends ConversionService {

	private static volatile SystemConversionService instance;

	public static SystemConversionService getInstance() {
		if (instance == null) {
			synchronized (SystemConversionService.class) {
				if (instance == null) {
					instance = new SystemConversionService();
					instance.getConverters().setServiceClass(ConversionService.class);
					instance.getConverters().configure();
				}
			}
		}
		return instance;
	}

	public SystemConversionService() {
		getConverters().register(new ObjectToCollectionConverter());
		getConverters().register(new ArrayToArrayConverter());
		getConverters().register(new ArrayToCollectionConverter());
		getConverters().register(new CollectionToArrayConverter());
		getConverters().register(new CollectionToCollectionConverter());
		getConverters().register(new CollectionToObjectConverter());
		getConverters().register(new MapToMapConverter());
		getConverters().register(new ObjectToArrayConverter());

		register(StringToBigDecimalConverter.DEFAULT);
		register(StringToBigIntegerConverter.DEFAULT);
		register(StringToBooleanConverter.DEFAULT);
		register(StringToByteConverter.DEFAULT);
		register(StringToCharacterConverter.DEFAULT);
		register(StringToCharsetConverter.DEFAULT);
		register(StringToClassConverter.DEFAULT);
		register(StringToCurrencyConverter.DEFAULT);
		register(StringToDoubleConverter.DEFAULT);
		register(StringToEnumConverter.DEFAULT);
		register(StringToFloatConverter.DEFAULT);
		register(StringToIntegerConverter.DEFAULT);
		register(StringToLocaleConverter.DEFAULT);
		register(StringToLongConverter.DEFAULT);
		register(StringToShortConverter.DEFAULT);
		register(StringToTimeZoneConverter.DEFAULT);
		register(NumberToEnumConverter.DEFAULT);
	}

	@Override
	public boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return Converter.assignable().canConvert(sourceTypeDescriptor, targetTypeDescriptor)
				|| super.canConvert(sourceTypeDescriptor, targetTypeDescriptor);
	}

	@Override
	public Object convert(Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		if (Converter.assignable().canConvert(sourceTypeDescriptor, targetTypeDescriptor)) {
			return source;
		}
		return super.convert(source, sourceTypeDescriptor, targetTypeDescriptor);
	}
}
