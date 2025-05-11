package run.soeasy.framework.core.transform;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypedValueAccessor;

/**
 * 映射器
 * 
 * @author wcnnkh
 *
 * @param <K>
 * @param <V>
 * @param <T>
 */
public interface Mapper<K, V extends TypedValueAccessor, T extends Mapping<K, V>> {
	boolean doMapping(@NonNull MappingContext<K, V, T> sourceContext, @NonNull MappingContext<K, V, T> targetContext);
}
