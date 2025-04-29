package run.soeasy.framework.core.mapping;

import java.util.Map;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ConversionService;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.TypedValueAccessor;
import run.soeasy.framework.core.convert.transform.Template;
import run.soeasy.framework.core.convert.value.ValueAccessor;
import run.soeasy.framework.core.transform.mapping.collection.MapPropertySource;

public class DefaultMapper
		extends ObjectMapper<Object, TypedValueAccessor, Template<Object, TypedValueAccessor>, ConversionException>
		implements ConversionService {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public DefaultMapper() {
		getObjectTemplateProvider().register(Template.class, (s, t) -> (Template) s);
		getObjectTemplateProvider().register(Map.class, (s, t) -> new MapPropertySource((Map) s, t));
		setTemplateTransformer(new ConfigurationProperties());
	}

	@Override
	public Object convert(@NonNull ValueAccessor value, @NonNull TypeDescriptor targetType) throws ConversionException {
		return value.map(targetType, this).orElse(null);
	}

}
