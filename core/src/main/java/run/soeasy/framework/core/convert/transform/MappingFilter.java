package run.soeasy.framework.core.convert.transform;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypedValueAccessor;

@FunctionalInterface
public interface MappingFilter<K, V extends TypedValueAccessor, T extends Mapping<K, V>> {

	boolean doMapping(@NonNull MappingContext<K, V, T> sourceContext, @NonNull MappingContext<K, V, T> targetContext,
			@NonNull Mapper<K, V, T> mapper);

}
