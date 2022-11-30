package io.basc.framework.convert.support;

import io.basc.framework.convert.lang.ConfigurableConversionService;
import io.basc.framework.convert.lang.DateFormatConversionService;
import io.basc.framework.convert.lang.JsonConversionService;
import io.basc.framework.convert.lang.JsonToObjectConversionService;
import io.basc.framework.convert.lang.StringConversionService;

public class DefaultConversionService extends ConfigurableConversionService {

	public DefaultConversionService() {
		addService(new ArrayToArrayConversionService(this));
		addService(new ArrayToCollectionConversionService(this));

		addService(new ByteBufferConversionService(this));

		addService(new CollectionToArrayConversionService(this));
		addService(new CollectionToCollectionConversionService(this));
		addService(new CollectionToObjectConversionService(this));

		addService(new DateFormatConversionService());
		addService(new LocalDateTimeConversion());

		addService(new MapToMapConversionService(this));

		addService(new ValueConversionService(this));
		addService(new JsonConversionService());
		addService(new JsonToObjectConversionService());

		addService(StringConversionService.DEFAULT);

		addService(new ObjectToArrayConversionService(this));
		addService(new ObjectToCollectionConversionService(this));
	}
}
