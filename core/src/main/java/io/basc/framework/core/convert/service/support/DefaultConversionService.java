package io.basc.framework.core.convert.service.support;

import io.basc.framework.core.convert.date.ConfigurableDateCodecResolver;
import io.basc.framework.core.convert.date.ConfigurableZoneOffsetResolver;
import io.basc.framework.core.convert.date.DateFormatConversionService;
import io.basc.framework.core.convert.date.LocalDateTimeConversion;
import io.basc.framework.core.convert.lang.StringConversionService;
import io.basc.framework.core.convert.service.ConversionService;
import io.basc.framework.core.convert.service.ConversionServices;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.reflect.ReflectionUtils;

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
