package io.basc.framework.core.convert.transform.stereotype;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.alias.NamingStrategy;
import io.basc.framework.util.collections.Elements;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class DefaultTemplateReader<K, SV extends Value, S extends Template<K, ? extends SV>, TV extends Accessor, T extends Template<K, ? extends TV>, E extends Throwable>
		extends FilterableTemplateReader<K, SV, S, TV, T, E, TemplateReadFilters<K, SV, S, TV, T, E>> {
	private NamingStrategy<K> namingStrategy;

	public DefaultTemplateReader() {
		super(new TemplateReadFilters<>());
	}

	@Override
	public Elements<? extends SV> readFrom(TemplateContext<K, SV, S> sourceContext, @NonNull S source,
			@NonNull TypeDescriptor sourceType, TemplateContext<K, TV, T> targetContext, @NonNull T target,
			@NonNull TypeDescriptor targetType, @NonNull K index, @NonNull TV targetAccessor) throws E {
		Elements<? extends SV> elements = super.readFrom(sourceContext, source, sourceType, targetContext, target,
				targetType, index, targetAccessor);
		if (namingStrategy != null) {
			elements = elements.toList();
			if (elements.isEmpty() && (targetAccessor.getRequiredTypeDescriptor().isMap()
					|| targetAccessor.getRequiredTypeDescriptor().isCollection())) {
				Elements<K> keys = source.getAccessorIndexes()
						.filter((e) -> namingStrategy.test(e) && namingStrategy.startsWith(e, index)).toList();
				if (!keys.isEmpty()) {
					List<KeyValue<K, SV>> keyValueList = new ArrayList<>();
					for (K key : keys) {
						Elements<? extends SV> values = super.readFrom(sourceContext, source, sourceType, targetContext,
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
