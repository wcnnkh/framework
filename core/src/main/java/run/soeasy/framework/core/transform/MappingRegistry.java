package run.soeasy.framework.core.transform;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.spi.ServiceMap;

@Getter
@Setter
public class MappingRegistry<S, K, V extends TypedValueAccessor, T extends Mapping<K, V>>
		extends ServiceMap<MappingFactory<S, K, V, T>> implements MappingProvider<S, K, V, T> {
	private MappingProvider<? super S, ? extends K, ? extends V, ? extends T> mappingProvider;

	@Override
	public T getMapping(@NonNull S source, @NonNull TypeDescriptor requiredType) {
		MappingFactory<? super S, ? extends K, ? extends V, ? extends T> factory = search(requiredType.getType())
				.first();
		if (factory == null) {
			return mappingProvider.getMapping(source, requiredType);
		}
		return factory.getMapping(source, requiredType);
	}

	@Override
	public boolean hasMapping(@NonNull TypeDescriptor requiredType) {
		return !search(requiredType.getType()).isEmpty() || mappingProvider.hasMapping(requiredType);
	}
}
