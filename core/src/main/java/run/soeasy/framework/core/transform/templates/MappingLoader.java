package run.soeasy.framework.core.transform.templates;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.spi.ServiceMap;

@Getter
@Setter
public class MappingLoader<K, V extends TypedValueAccessor, T extends Mapping<K, V>>
		implements MappingProvider<Object, K, V, T> {
	private final ServiceMap<MappingFactory<Object, K, V, T>> registry = new ServiceMap<>();

	@Override
	public T getMapping(@NonNull Object source, @NonNull TypeDescriptor requiredType) {
		MappingFactory<? super Object, ? extends K, ? extends V, ? extends T> factory = registry
				.search(requiredType.getType()).first();
		if (factory == null) {
			return null;
		}
		return factory.getMapping(source, requiredType);
	}

	@Override
	public boolean hasMapping(@NonNull TypeDescriptor requiredType) {
		return !registry.search(requiredType.getType()).isEmpty();
	}

	@SuppressWarnings("unchecked")
	public <S> void registerFactory(Class<S> requriedType, MappingFactory<S, K, V, T> mappingFactory) {
		registry.register(requriedType, (MappingFactory<Object, K, V, T>) mappingFactory);
	}
}
