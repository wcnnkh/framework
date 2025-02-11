package io.basc.framework.core.convert.transform.mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.transform.stereotype.Accessor;
import io.basc.framework.core.convert.transform.stereotype.Template;
import io.basc.framework.core.convert.transform.stereotype.TemplateContext;
import io.basc.framework.core.convert.transform.stereotype.TemplateReadFilter;
import io.basc.framework.core.convert.transform.stereotype.TemplateReader;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.alias.NamingStrategy;
import io.basc.framework.util.collections.Elements;
import lombok.NonNull;

public class ConfigurationProperties<K> implements
		TemplateReadFilter<K, Accessor, Template<K, ? extends Accessor>, Accessor, Template<K, ? extends Accessor>, ConversionException> {
	private NamingStrategy<K> namingStrategy;

	@Override
	public Elements<? extends Accessor> readFrom(
			TemplateContext<K, Accessor, Template<K, ? extends Accessor>> sourceContext,
			@NonNull Template<K, ? extends Accessor> source, @NonNull TypeDescriptor sourceType,
			TemplateContext<K, Accessor, Template<K, ? extends Accessor>> targetContext,
			@NonNull Template<K, ? extends Accessor> target, @NonNull TypeDescriptor targetType, @NonNull K index,
			@NonNull Accessor targetAccessor,
			TemplateReader<K, Accessor, Template<K, ? extends Accessor>, Accessor, Template<K, ? extends Accessor>, ConversionException> templateReader)
			throws ConversionException {
		Elements<? extends Accessor> elements = templateReader.readFrom(sourceContext, source, sourceType,
				targetContext, target, targetType, index, targetAccessor);
		if (namingStrategy != null) {
			elements = elements.toList();
			if (elements.isEmpty() && (targetAccessor.getRequiredTypeDescriptor().isMap()
					|| targetAccessor.getRequiredTypeDescriptor().isCollection())) {
				Elements<K> keys = source.getAccessorIndexes()
						.filter((e) -> namingStrategy.test(e) && namingStrategy.startsWith(e, index)).toList();
				if (!keys.isEmpty()) {
					List<KeyValue<K, Accessor>> keyValueList = new ArrayList<>();
					for (K key : keys) {
						Elements<? extends Accessor> values = super.readFrom(sourceContext, source, sourceType, targetContext,
								target, targetType, key, targetAccessor);
						values.forEach((value) -> keyValueList.add(KeyValue.of(key, value)));
					}

					if (targetAccessor.getRequiredTypeDescriptor().isCollection()) {
						List<SV> list = keyValueList.stream().map((e) -> e.getValue()).collect(Collectors.toList());
					}
				}
			}
		}
		return elements;
	}

}
