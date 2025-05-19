package run.soeasy.framework.core.transform;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;

/**
 * 基础的模板写入实现
 * 
 * @author soeasy.run
 *
 * @param <K>
 * @param <V>
 * @param <T>
 */
@Getter
public class GenericMapper<K, V extends TypedValueAccessor, T extends Mapping<K, V>> implements Mapper<K, V, T> {
	private final ValueMapper<K, V, T> valueMapper = new ValueMapper<>();
	private final IterativeMapper<K, V, T> iterativeMapper = new IterativeMapper<>(valueMapper);
	private final RandomAccessMapper<K, V, T> randomAccessMapper = new RandomAccessMapper<>(valueMapper);

	public boolean doMapping(@NonNull MappingContext<K, V, T> sourceContext,
			@NonNull MappingContext<K, V, T> targetContext) {
		if (sourceContext.hasKeyValue() && targetContext.hasKeyValue()) {
			return valueMapper.doMapping(sourceContext, targetContext);
		} else if (sourceContext.hasMapping() && targetContext.hasMapping()) {
			if (sourceContext.getMapping().isRandomAccess()) {
				return randomAccessMapper.doMapping(sourceContext, targetContext);
			} else {
				return iterativeMapper.doMapping(sourceContext, targetContext);
			}
		}
		return false;
	}
}
