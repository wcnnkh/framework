package io.basc.framework.core.convert.transform.stractegy;

import io.basc.framework.core.convert.ConversionService;
import io.basc.framework.core.convert.IdentityConversionService;
import io.basc.framework.core.convert.transform.Access;
import io.basc.framework.core.convert.transform.Mapping;
import io.basc.framework.core.convert.transform.MappingContext;
import io.basc.framework.util.Elements;
import io.basc.framework.util.KeyValue;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class DefaultMappingStrategy<K, V extends Access, M extends Mapping<K, V>, E extends Throwable>
		implements MappingStrategy<K, V, M, E> {
	@NonNull
	private ConversionService conversionService = new IdentityConversionService();

	@Override
	public void doMapping(MappingContext<K, V, M> sourceContext, @NonNull M sourceMapping,
			@NonNull KeyValue<K, V> entry, MappingContext<K, V, M> targetContext, @NonNull M targetMapping) throws E {
		if (!entry.getValue().isReadable()) {
			return;
		}

		Elements<V> accesses = targetMapping.getAccesses(entry.getKey());
		for (V access : accesses) {
			if (access.isWriteable()) {
				continue;
			}

			if (!conversionService.canConvert(entry.getValue().getTypeDescriptor(),
					access.getRequiredTypeDescriptor())) {
				continue;
			}

			Object source = entry.getValue().get();
			if (source == null) {
				continue;
			}

			source = conversionService.convert(source, entry.getValue().getTypeDescriptor(),
					access.getRequiredTypeDescriptor());
			if (source == null) {
				continue;
			}
			access.set(source);
		}
	}

}
