package run.soeasy.framework.core.convert.transform.mapping;

import java.util.Map;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.service.ConversionService;
import run.soeasy.framework.core.convert.transform.stereotype.Accessor;
import run.soeasy.framework.core.convert.transform.stereotype.Template;
import run.soeasy.framework.core.convert.transform.stereotype.collection.MapProperties;

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
		return value.map(targetType, this).orElse(null);
	}

}
