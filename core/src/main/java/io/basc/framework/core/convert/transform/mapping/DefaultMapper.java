package io.basc.framework.core.convert.transform.mapping;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import io.basc.framework.core.convert.config.ConditionalConversionService;
import io.basc.framework.core.convert.config.ConvertiblePair;
import io.basc.framework.core.convert.transform.stereotype.Accessor;
import io.basc.framework.core.convert.transform.stereotype.Template;
import io.basc.framework.core.convert.transform.stereotype.TemplateContext;
import io.basc.framework.core.convert.transform.stereotype.TemplateWriter;
import io.basc.framework.core.convert.transform.stereotype.collection.MapProperties;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.alias.NamingStrategy;
import io.basc.framework.util.collections.Elements;
import lombok.NonNull;

public class DefaultMapper
		extends ObjectMapper<Object, Accessor, Template<Object, ? extends Accessor>, ConversionException>
		implements ConditionalConversionService {
	private NamingStrategy namingStrategy;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public DefaultMapper() {
		getObjectTemplateProvider().register(Template.class, (s, t) -> (Template) s);
		getObjectTemplateProvider().register(Map.class, (s, t) -> new MapProperties((Map) s, t, this));
	}

	@Override
	protected int writeTo(TemplateContext<Object, Accessor, Template<Object, ? extends Accessor>> sourceContext,
			@NonNull Template<Object, ? extends Accessor> source, @NonNull TypeDescriptor sourceType,
			TemplateContext<Object, Accessor, Template<Object, ? extends Accessor>> targetContext,
			@NonNull Template<Object, ? extends Accessor> target, @NonNull TypeDescriptor targetType,
			@NonNull Object index, List<? extends Accessor> sourceElements, Accessor targetAccessor,
			@NonNull TemplateWriter<Object, Accessor, Template<Object, ? extends Accessor>, Accessor, Template<Object, ? extends Accessor>, ? extends ConversionException> templateWriter)
			throws ConversionException {
		int count = super.writeTo(sourceContext, source, sourceType, targetContext, target, targetType, index,
				sourceElements, targetAccessor, templateWriter);
		if (count != 0) {
			return count;
		}

		if (index instanceof String) {
			if (namingStrategy == null) {
				return 0;
			}

			String pattern = (String) index;
			if (targetAccessor.getRequiredTypeDescriptor().isMap()
					|| targetAccessor.getRequiredTypeDescriptor().isCollection()) {
				Elements<String> keys = source.getAccessorIndexes().filter((e) -> (e instanceof String))
						.map((e) -> (String) e).filter((e) -> namingStrategy.match(pattern, e));
				Elements<KeyValue<String, Accessor>> keyValues = keys.flatMap(
						(e) -> readFrom(sourceContext, source, sourceType, targetContext, target, targetType, e)
								.map((v) -> KeyValue.of(e, v)));
				if (targetAccessor.getRequiredTypeDescriptor().isCollection()) {
					List<Accessor> list = keyValues.map((e) -> e.getValue()).toList();
					Accessor sourceAccessor = Accessor.of(Value.of(list));
					return super.writeTo(sourceContext, source, sourceType, targetContext, target, targetType, index,
							Arrays.asList(sourceAccessor), targetAccessor, templateWriter);
				}
			}
		}
		return 0;
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
