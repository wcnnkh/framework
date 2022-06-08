package io.basc.framework.convert;

import io.basc.framework.lang.Nullable;

/**
 * 和{@link Converter}的行为相反,定义此类是为了方便同时存在的情况
 * 
 * @author wcnnkh
 *
 * @param <S>
 * @param <T>
 * @param <E>
 */
public interface Inverter<S, T, E extends Throwable> {
	default <R extends T> R invert(@Nullable S source, Class<? extends R> targetType) throws E {
		return invert(source, TypeDescriptor.forObject(source), targetType);
	}

	default <R extends T> R invert(@Nullable S source, @Nullable Class<? extends S> sourceType,
			Class<? extends R> targetType) throws E {
		return invert(source, TypeDescriptor.valueOf(sourceType), targetType);
	}

	default <R extends T> R invert(@Nullable S source, @Nullable Class<? extends S> sourceType,
			TypeDescriptor targetType) throws E {
		return invert(source, TypeDescriptor.valueOf(sourceType), targetType);
	}

	default <R extends T> R invert(@Nullable S source, TypeDescriptor targetType) throws E {
		return invert(source, TypeDescriptor.forObject(source), targetType);
	}

	default <R extends T> R invert(@Nullable S source, @Nullable TypeDescriptor sourceType,
			Class<? extends R> targetType) throws E {
		return invert(source, sourceType, TypeDescriptor.valueOf(targetType));
	}

	<R extends T> R invert(@Nullable S source, @Nullable TypeDescriptor sourceType, TypeDescriptor targetType) throws E;
}
