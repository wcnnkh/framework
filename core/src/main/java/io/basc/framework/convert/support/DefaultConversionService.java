package io.basc.framework.convert.support;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.lang.ConfigurableConversionService;
import io.basc.framework.convert.lang.DateFormatConversionService;
import io.basc.framework.convert.lang.JsonConversionService;
import io.basc.framework.convert.lang.JsonToObjectConversionService;
import io.basc.framework.convert.lang.StringConversionService;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.util.ClassUtils;

public class DefaultConversionService extends ConfigurableConversionService {

	public DefaultConversionService() {
		registerService(new ArrayToArrayConversionService(this));
		registerService(new ArrayToCollectionConversionService(this));

		registerService(new ByteBufferConversionService(this));

		registerService(new CollectionToArrayConversionService(this));
		registerService(new CollectionToCollectionConversionService(this));
		registerService(new CollectionToObjectConversionService(this));

		registerService(new DateFormatConversionService());
		registerService(new LocalDateTimeConversion());

		registerService(new MapToMapConversionService(this));

		registerService(new ValueConversionService(this));
		registerService(new JsonConversionService());
		registerService(new JsonToObjectConversionService());

		registerService(StringConversionService.DEFAULT);

		registerService(new ObjectToArrayConversionService(this));
		registerService(new ObjectToCollectionConversionService(this));

		// 并非所有的环境都支持sql类型
		if (ClassUtils.isPresent("io.basc.framework.convert.lang.SqlDateConversionService", null)) {
			registerService((ConversionService) ReflectionUtils
					.newInstance(ClassUtils.getClass("io.basc.framework.convert.lang.SqlDateConversionService", null)));
		}
	}
}
