package io.basc.framework.core.convert.transform.stractegy;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.transform.Access;
import io.basc.framework.core.convert.transform.Mapping;
import io.basc.framework.core.convert.transform.MappingFactory;
import io.basc.framework.core.convert.transform.Transformer;
import io.basc.framework.util.Registration;
import io.basc.framework.util.spi.ServiceMap;

public class DefaultMappingFactory<T, K, V extends Access, M extends Mapping<K, V>, E extends Throwable> extends
		DefaultMappingStrategyFactory<K, V, M, E> implements MappingFactory<T, K, V, M, E>, Transformer<T, T, E> {
	private final ServiceMap<MappingFactory<?, K, V, M, E>> mappingFactoryRegistry = new ServiceMap<>();

	@SuppressWarnings("unchecked")
	@Override
	public M getMapping(T transform, TypeDescriptor typeDescriptor) {
		MappingFactory<T, K, V, M, E> mappingFactory = (MappingFactory<T, K, V, M, E>) getMappingFactory(
				typeDescriptor.getType());
		if (mappingFactory == null) {
			return null;
		}
		return mappingFactory.getMapping(transform, typeDescriptor);
	}

	@SuppressWarnings("unchecked")
	public <R> MappingFactory<R, K, V, M, E> getMappingFactory(Class<R> requiredType) {
		return (MappingFactory<R, K, V, M, E>) mappingFactoryRegistry.getFirst(requiredType);
	}

	public <R extends T> Registration registerMappingFactory(Class<R> requiredType,
			MappingFactory<R, K, V, M, E> mappingFactory) {
		return mappingFactoryRegistry.register(requiredType, mappingFactory);
	}

	@Override
	public boolean canTransform(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return getMappingFactory(sourceType.getType()) != null && getMappingFactory(targetType.getType()) != null;
	}

	@Override
	public void transform(T source, TypeDescriptor sourceType, T target, TypeDescriptor targetType) throws E {
		MappingStrategy<K, V, M, E> mappingStrategy = getMappingStrategy(targetType);
		transform(source, sourceType, target, targetType, mappingStrategy);
	}

	public void transform(T source, TypeDescriptor sourceType, T target, TypeDescriptor targetType,
			MappingStrategy<K, V, M, E> mappingStrategy) throws E {
		M sourceMapping = getMapping(source, sourceType);
		M targetMapping = getMapping(target, targetType);
		mappingStrategy.doMapping(sourceMapping, targetMapping);
	}
}
