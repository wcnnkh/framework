package run.soeasy.framework.core.transform;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypedValueAccessor;
import run.soeasy.framework.core.spi.ConfigurableServices;

public class MappingFilters<K, V extends TypedValueAccessor, T extends Mapping<K, V>>
		extends ConfigurableServices<MappingFilter<K, V, T>> implements MappingFilter<K, V, T> {

	@Override
	public boolean doMapping(@NonNull MappingContext<K, V, T> sourceContext,
			@NonNull MappingContext<K, V, T> targetContext, @NonNull Mapper<K, V, T> mapper) {
		ChainMapper<K, V, T> chain = new ChainMapper<>(this.iterator(), mapper);
		return chain.doMapping(sourceContext, targetContext);
	}
}
