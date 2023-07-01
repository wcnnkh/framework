package io.basc.framework.convert.support;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.config.support.ConfigurableConversionService;
import io.basc.framework.convert.lang.DateFormatConversionService;
import io.basc.framework.convert.lang.JsonConversionService;
import io.basc.framework.convert.lang.JsonToObjectConversionService;
import io.basc.framework.convert.lang.StringConversionService;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.util.ClassUtils;

public class DefaultConversionService extends ConfigurableConversionService {

	public DefaultConversionService() {
		getRegistry().register(new ArrayToArrayConversionService(this));
		getRegistry().register(new ArrayToCollectionConversionService(this));

		getRegistry().register(new ByteBufferConversionService(this));

		getRegistry().register(new CollectionToArrayConversionService(this));
		getRegistry().register(new CollectionToCollectionConversionService(this));
		getRegistry().register(new CollectionToObjectConversionService(this));

		getRegistry().register(new DateFormatConversionService());
		getRegistry().register(new LocalDateTimeConversion());

		getRegistry().register(new MapToMapConversionService(this));

		getRegistry().register(new ValueConversionService(this));
		getRegistry().register(new JsonConversionService());
		getRegistry().register(new JsonToObjectConversionService());

		getRegistry().register(StringConversionService.DEFAULT);

		getRegistry().register(new ObjectToArrayConversionService(this));
		getRegistry().register(new ObjectToCollectionConversionService(this));

		// 并非所有的环境都支持sql类型
		if (ClassUtils.isPresent("io.basc.framework.convert.lang.SqlDateConversionService", null)) {
			getRegistry().register((ConversionService) ReflectionUtils
					.newInstance(ClassUtils.getClass("io.basc.framework.convert.lang.SqlDateConversionService", null)));
		}
	}
}
