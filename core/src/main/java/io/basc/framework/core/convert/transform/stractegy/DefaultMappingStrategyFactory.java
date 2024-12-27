package io.basc.framework.core.convert.transform.stractegy;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.transform.Accesstor;
import io.basc.framework.core.convert.transform.Mapping;
import io.basc.framework.util.sequences.uuid.UUIDSequences;
import lombok.NonNull;

public class DefaultMappingStrategyFactory<K, V extends Accesstor, M extends Mapping<K, V>, E extends Throwable>
		implements MappingStrategyFactory<K, V, M, E> {
	private static class InternalMappingStrategy<K, V extends Accesstor, M extends Mapping<K, V>, E extends Throwable>
			extends FilterableMappingStrategy<K, V, M, E> {
		private final String id;

		public InternalMappingStrategy(@NonNull Iterable<? extends MappingFilter<K, V, M, E>> mappingFilters,
				MappingStrategy<K, V, M, E> dottomlessStrategy, @NonNull String id) {
			super(mappingFilters, dottomlessStrategy);
			this.id = id;
		}
	}

	private final String id = UUIDSequences.getInstance().next();
	private final DefaultMappingStrategy<K, V, M, E> dottomlessMappingStrategy = new DefaultMappingStrategy<>();
	private final MappingFilters<K, V, M, E> mappingFilters = new MappingFilters<>();

	@Override
	public MappingStrategy<K, V, M, E> getMappingStrategy(@NonNull TypeDescriptor targetType) {
		return wrap(dottomlessMappingStrategy);
	}

	protected MappingStrategy<K, V, M, E> wrap(MappingStrategy<K, V, M, E> mappingStrategy) {
		if (mappingStrategy instanceof InternalMappingStrategy) {
			if (((InternalMappingStrategy<K, V, M, E>) mappingStrategy).id.equals(this.id)) {
				return mappingStrategy;
			}
		}
		return new InternalMappingStrategy<>(mappingFilters, dottomlessMappingStrategy, id);
	}
}
