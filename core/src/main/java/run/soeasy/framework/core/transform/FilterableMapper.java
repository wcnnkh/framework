package run.soeasy.framework.core.transform;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;

@RequiredArgsConstructor
@Getter
public class FilterableMapper<K, V extends TypedValueAccessor, T extends Mapping<K, V>> implements Mapper<K, V, T> {
	@NonNull
	private final Iterable<MappingFilter<K, V, T>> filters;
	@NonNull
	private final Mapper<K, V, T> mapper;

	@Override
	public boolean doMapping(@NonNull MappingContext<K, V, T> sourceContext,
			@NonNull MappingContext<K, V, T> targetContext) {
		ChainMapper<K, V, T> chain = new ChainMapper<>(filters.iterator(), mapper);
		return chain.doMapping(sourceContext, targetContext);
	}

	public final boolean doMapping(@NonNull MappingContext<K, V, T> sourceContext,
			@NonNull MappingContext<K, V, T> targetContext, @NonNull Iterable<MappingFilter<K, V, T>> filters) {
		FilterableMapper<K, V, T> templateWriter = new FilterableMapper<>(filters, this);
		return templateWriter.doMapping(sourceContext, targetContext);
	}
}
