package run.soeasy.framework.core.convert.support;

import run.soeasy.framework.core.convert.ConifgurableConversionService;
import run.soeasy.framework.core.convert.date.ConfigurableDateCodecResolver;
import run.soeasy.framework.core.convert.date.ConfigurableZoneOffsetResolver;
import run.soeasy.framework.core.convert.date.DateFormatConversionService;
import run.soeasy.framework.core.convert.date.LocalDateTimeConversion;
import run.soeasy.framework.core.convert.strings.StringConversionService;

/**
 * 全局的ConversionService
 * 
 * @author shuchaowen
 *
 */
public class SystemConversionService extends ConifgurableConversionService {

	private static volatile SystemConversionService instance;

	public static SystemConversionService getInstance() {
		if (instance == null) {
			synchronized (SystemConversionService.class) {
				if (instance == null) {
					instance = new SystemConversionService();
					instance.configure();
				}
			}
		}
		return instance;
	}

	public SystemConversionService() {
		register(new ArrayToArrayConversionService(this));
		register(new ArrayToCollectionConversionService(this));

		register(new ByteBufferConversionService(this));

		register(new CollectionToArrayConversionService(this));
		register(new CollectionToCollectionConversionService(this));
		register(new CollectionToObjectConversionService(this));

		register(new DateFormatConversionService(ConfigurableDateCodecResolver.getInstance()));
		register(new LocalDateTimeConversion(ConfigurableZoneOffsetResolver.getInstance()));

		register(new MapToMapConversionService(this));

		register(StringConversionService.DEFAULT);

		register(new ObjectToArrayConversionService(this));
		register(new ObjectToCollectionConversionService(this));
	}
}
