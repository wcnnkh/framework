package io.basc.framework.convert.support;

import io.basc.framework.beans.factory.spi.SPI;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.config.support.ConfigurableConversionService;
import io.basc.framework.convert.lang.DateFormatConversionService;
import io.basc.framework.convert.lang.JsonConversionService;
import io.basc.framework.convert.lang.JsonToObjectConversionService;
import io.basc.framework.convert.lang.StringConversionService;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.util.ClassUtils;

/**
 * 全局的ConversionService
 * 
 * @author shuchaowen
 *
 */
public final class GlobalConversionService extends ConfigurableConversionService {

	private static volatile GlobalConversionService instance;

	public static GlobalConversionService getInstance() {
		if (instance == null) {
			synchronized (GlobalConversionService.class) {
				if (instance == null) {
					instance = new GlobalConversionService();
					instance.configure(SPI.global());
				}
			}
		}
		return instance;
	}

	private GlobalConversionService() {
		register(new ArrayToArrayConversionService(this));
		register(new ArrayToCollectionConversionService(this));

		register(new ByteBufferConversionService(this));

		register(new CollectionToArrayConversionService(this));
		register(new CollectionToCollectionConversionService(this));
		register(new CollectionToObjectConversionService(this));

		register(new DateFormatConversionService());
		register(new LocalDateTimeConversion());

		register(new MapToMapConversionService(this));

		register(new ValueConversionService());
		register(new JsonConversionService());
		register(new JsonToObjectConversionService());

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
