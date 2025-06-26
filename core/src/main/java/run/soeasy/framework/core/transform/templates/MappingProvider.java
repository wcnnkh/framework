package run.soeasy.framework.core.transform.templates;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.spi.ServiceMap;

@Getter
@Setter
public class MappingProvider<K, V extends TypedValueAccessor, T extends Mapping<K, V>>
		extends ServiceMap<MappingFactory<Object, K, V, T>> implements MappingFactory<Object, K, V, T> {

	@Override
	public T getMapping(@NonNull Object source, @NonNull TypeDescriptor requiredType) {
		MappingFactory<? super Object, ? extends K, ? extends V, ? extends T> factory = assignableFrom(requiredType.getType())
				.first();
		if (factory == null) {
			return null;
		}
		return factory.getMapping(source, requiredType);
	}

	@Override
	public boolean hasMapping(@NonNull TypeDescriptor requiredType) {
		return !assignableFrom(requiredType.getType()).isEmpty();
	}

	@SuppressWarnings("unchecked")
	public <S> void registerFactory(Class<S> requriedType, MappingFactory<S, K, V, T> mappingFactory) {
		register(requriedType, (MappingFactory<Object, K, V, T>) mappingFactory);
	}
}
