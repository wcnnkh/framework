package run.soeasy.framework.core.convert.transform;

import java.util.Iterator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import run.soeasy.framework.core.convert.TypedValueAccessor;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
class ChainMapper<K, V extends TypedValueAccessor, T extends Mapping<K, V>> implements Mapper<K, V, T> {
	@NonNull
	private final Iterator<? extends MappingFilter<K, V, T>> iterator;
	private Mapper<K, V, T> mapper;

	@Override
	public boolean doMapping(@NonNull MappingContext<K, V, T> sourceContext,
			@NonNull MappingContext<K, V, T> targetContext) {
		if (iterator.hasNext()) {
			return iterator.next().doMapping(sourceContext, targetContext, this);
		} else if (mapper != null) {
			return mapper.doMapping(sourceContext, targetContext);
		}
		return false;
	}
}
