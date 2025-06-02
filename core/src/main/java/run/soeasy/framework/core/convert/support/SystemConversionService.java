package run.soeasy.framework.core.convert.support;

import run.soeasy.framework.core.convert.ConversionService;
import run.soeasy.framework.core.convert.ConverterRegistry;
import run.soeasy.framework.core.convert.strings.StringConverter;

/**
 * 全局的ConversionService
 * 
 * @author shuchaowen
 *
 */
public class SystemConversionService extends ConverterRegistry {

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
		getConverters().register(new ArrayToArrayConversionService(this));
		getConverters().register(new ArrayToCollectionConversionService(this));

		getConverters().register(new CollectionToArrayConversionService(this));
		getConverters().register(new CollectionToCollectionConversionService(this));
		getConverters().register(new CollectionToObjectConversionService(this));

		getConverters().register(new MapToMapConversionService(this));

		getConverters().register(StringConverter.getInstance());

		getConverters().register(new ObjectToArrayConversionService(this));
		getConverters().register(new ObjectToCollectionConversionService(this));
	}
}
