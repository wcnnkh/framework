package run.soeasy.framework.core.convert.support;

import run.soeasy.framework.core.convert.ConversionService;

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
}
