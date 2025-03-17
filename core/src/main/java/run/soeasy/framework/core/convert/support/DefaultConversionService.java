package run.soeasy.framework.core.convert.support;

import run.soeasy.framework.core.convert.service.ConversionService;
import run.soeasy.framework.core.convert.service.ConversionServices;
import run.soeasy.framework.core.convert.support.date.ConfigurableDateCodecResolver;
import run.soeasy.framework.core.convert.support.date.ConfigurableZoneOffsetResolver;
import run.soeasy.framework.core.convert.support.date.DateFormatConversionService;
import run.soeasy.framework.core.convert.support.date.LocalDateTimeConversion;
import run.soeasy.framework.core.convert.support.strings.StringConversionService;
import run.soeasy.framework.util.ClassUtils;
import run.soeasy.framework.util.reflect.ReflectionUtils;

/**
 * 全局的ConversionService
 * 
 * @author shuchaowen
 *
 */
public class DefaultConversionService extends ConversionServices {

	private static volatile DefaultConversionService instance;

	public static DefaultConversionService getInstance() {
		if (instance == null) {
			synchronized (DefaultConversionService.class) {
				if (instance == null) {
					instance = new DefaultConversionService();
					instance.doNativeConfigure();
				}
			}
		}
		return instance;
	}

	public DefaultConversionService() {
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

		// 并非所有的环境都支持sql类型
		if (ClassUtils.isPresent("io.basc.framework.convert.lang.SqlDateConversionService", null)) {
			register((ConversionService) ReflectionUtils
					.newInstance(ClassUtils.getClass("io.basc.framework.convert.lang.SqlDateConversionService", null)));
		}
	}
}
