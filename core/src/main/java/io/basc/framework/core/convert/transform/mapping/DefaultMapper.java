package io.basc.framework.core.convert.transform.mapping;

import java.util.Map;
import java.util.Set;

import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import io.basc.framework.core.convert.config.ConditionalConversionService;
import io.basc.framework.core.convert.config.ConvertiblePair;
import io.basc.framework.core.convert.transform.stereotype.Accessor;
import io.basc.framework.core.convert.transform.stereotype.Template;
import io.basc.framework.core.convert.transform.stereotype.collection.MapProperties;
import lombok.NonNull;

public class DefaultMapper
		extends ObjectMapper<Object, Accessor, Template<Object, ? extends Accessor>, ConversionException>
		implements ConditionalConversionService {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public DefaultMapper() {
		getObjectTemplateProvider().register(Template.class, (s, t) -> (Template) s);
		getObjectTemplateProvider().register(Map.class, (s, t) -> new MapProperties((Map) s, t, this));
	}

	@Override
	public Object convert(@NonNull Value value, @NonNull TypeDescriptor targetType) throws ConversionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		// TODO Auto-generated method stub
		return null;
	}
}
