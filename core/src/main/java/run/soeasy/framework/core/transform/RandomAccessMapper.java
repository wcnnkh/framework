package run.soeasy.framework.core.transform;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.KeyValue;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;

/**
 * 随机读写映射的实现
 * 
 * @author soeasy.run
 *
 * @param <K>
 * @param <V>
 * @param <T>
 */
@RequiredArgsConstructor
@Getter
public class RandomAccessMapper<K, V extends TypedValueAccessor, T extends Mapping<K, V>> implements Mapper<K, V, T> {
	@NonNull
	private final Mapper<K, V, T> valueMapper;

	public boolean doMapping(@NonNull MappingContext<K, V, T> sourceContext,
			@NonNull MappingContext<K, V, T> targetContext) {
		if (sourceContext.hasKeyValue()
				|| targetContext.hasKeyValue() && !(sourceContext.hasMapping() && targetContext.hasMapping())) {
			return false;
		}

		int count = 0;
		for (KeyValue<K, V> target : targetContext.getMapping().getElements()) {
			Elements<V> sourceElements = sourceContext.getMapping().getValues(target.getKey());
			for (V value : sourceElements) {
				if (valueMapper.doMapping(sourceContext.current(KeyValue.of(target.getKey(), value)),
						targetContext.current(target))) {
					count++;
				}
			}
		}
		return count > 0;
	}
}
