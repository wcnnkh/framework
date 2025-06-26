package run.soeasy.framework.core.convert.support;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ConversionService;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.TypeDescriptor;

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
		getConverters().register(new ArrayToArrayConverter());
		getConverters().register(new ArrayToCollectionConverter());
		getConverters().register(new CollectionToArrayConverter());
		getConverters().register(new CollectionToCollectionConverter());
		getConverters().register(new CollectionToObjectConverter());
		getConverters().register(new MapToMapConverter());
		getConverters().register(new ObjectToArrayConverter());
		getConverters().register(new ObjectToCollectionConverter());
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
