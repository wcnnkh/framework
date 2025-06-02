package run.soeasy.framework.core.transform.templates;

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
public class GenericMapper<K, V extends TypedValueAccessor, T extends Mapping<K, V>> extends FilterableMapper<K, V, T> {
	private final ArrayMapper<K, V, T> arrayMapper = new ArrayMapper<>(this);
	private final MapMapper<K, V, T> mapMapper = new MapMapper<>(this);

	public GenericMapper(@NonNull Iterable<MappingFilter<K, V, T>> filters, Mapper<K, V, T> mapper) {
		super(filters, mapper);
	}

	public boolean doMapping(@NonNull MappingContext<K, V, T> sourceContext,
			@NonNull MappingContext<K, V, T> targetContext) {
		if (sourceContext.hasKeyValue() && targetContext.hasKeyValue()) {
			return super.doMapping(sourceContext, targetContext);
		} else if (sourceContext.hasMapping() && targetContext.hasMapping()) {
			if (sourceContext.getMapping().isMap()) {
				return mapMapper.doMapping(sourceContext, targetContext);
			} else {
				return arrayMapper.doMapping(sourceContext, targetContext);
			}
		}
		return super.doMapping(sourceContext, targetContext);
	}
}
