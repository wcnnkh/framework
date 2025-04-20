package run.soeasy.framework.core.transform;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;

@FunctionalInterface
public interface Transformer<S, T, E extends Throwable> {

	default boolean canTransform(@NonNull TypeDescriptor sourceType, @NonNull TypeDescriptor targetType) {
		return true;
	}

	/**
	 * 执行传输
	 * 
	 * @param source
	 * @param sourceType
	 * @param target
	 * @param targetType
	 * @return 是否成功
	 * @throws E
	 */
	boolean transform(@NonNull S source, @NonNull TypeDescriptor sourceType, @NonNull T target,
			@NonNull TypeDescriptor targetType) throws E;
}
