package io.basc.framework.core.convert.transform.mapping;

import java.util.Map;

import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Source;
import io.basc.framework.core.convert.config.ConversionService;
import io.basc.framework.core.convert.transform.stereotype.Accessor;
import io.basc.framework.core.convert.transform.stereotype.Template;
import io.basc.framework.core.convert.transform.stereotype.collection.MapProperties;
import lombok.NonNull;

public class DefaultMapper
		extends ObjectMapper<Object, Accessor, Template<Object, ? extends Accessor>, ConversionException>
		implements ConversionService {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public DefaultMapper() {
		getObjectTemplateProvider().register(Template.class, (s, t) -> (Template) s);
		getObjectTemplateProvider().register(Map.class, (s, t) -> new MapProperties((Map) s, t, this));
		setTemplateTransformer(new ConfigurationProperties());
	}

	@Override
	public Object convert(@NonNull Source value, @NonNull TypeDescriptor targetType) throws ConversionException {
		return value.getAsObject(targetType, this);
	}

}
