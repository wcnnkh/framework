package run.soeasy.framework.core.transform;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.domain.KeyValue;

/**
 * 迭代的方式映射
 * 
 * @author soeasy.run
 *
 * @param <K>
 * @param <V>
 * @param <T>
 */
@RequiredArgsConstructor
@Getter
@Setter
public class ArrayMapper<K, V extends TypedValueAccessor, T extends Mapping<K, V>> implements Mapper<K, V, T> {
	@NonNull
	private Mapper<K, V, T> valueMapper;

	public boolean doMapping(@NonNull MappingContext<K, V, T> sourceContext,
			@NonNull MappingContext<K, V, T> targetContext) {
		if (sourceContext.hasKeyValue()
				|| targetContext.hasKeyValue() && !(sourceContext.hasMapping() && targetContext.hasMapping())) {
			return false;
		}

		List<KeyValue<K, V>> sourceList = sourceContext.getMapping().getElements().collect(Collectors.toList());
		if (sourceList.isEmpty()) {
			return false;
		}

		int count = 0;
		for (KeyValue<K, V> target : targetContext.getMapping().getElements()) {
			Iterator<KeyValue<K, V>> sourceIterator = sourceList.iterator();
			while (sourceIterator.hasNext()) {
				KeyValue<K, V> source = sourceIterator.next();
				if (valueMapper.doMapping(sourceContext.current(source), targetContext.nested(target))) {
					sourceIterator.remove();
					count++;
				}
			}
		}
		return count > 0;
	}
}
