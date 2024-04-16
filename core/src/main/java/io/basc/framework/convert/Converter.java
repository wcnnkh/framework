package io.basc.framework.convert;

import io.basc.framework.lang.Nullable;

public interface Converter<S, T, E extends Throwable> {

	/**
	 * 默认无需实现，直接调用{@link #convert(Object, TypeDescriptor, Class)}
	 * 
	 * @see #convert(Object, TypeDescriptor, Class)
	 * @param <R>
	 * @param source
	 * @param targetType
	 * @return
	 * @throws E
	 */
	default <R extends T> R convert(@Nullable S source, Class<? extends R> targetType) throws E {
		return convert(source, TypeDescriptor.forObject(source), targetType);
	}

	/**
	 * 默认无需实现，直接调用{@link #convert(Object, TypeDescriptor, Class)}
	 * 
	 * @see #convert(Object, TypeDescriptor, Class)
	 * @param <R>
	 * @param source
	 * @param sourceType
	 * @param targetType
	 * @return
	 * @throws E
	 */
	default <R extends T> R convert(@Nullable S source, @Nullable Class<? extends S> sourceType,
			Class<? extends R> targetType) throws E {
		return convert(source, TypeDescriptor.valueOf(sourceType), targetType);
	}

	/**
	 * 默认无需实现，直接调用{@link #convert(Object, TypeDescriptor, TypeDescriptor)}
	 * 
	 * @see #convert(Object, TypeDescriptor, TypeDescriptor)
	 * @param source
	 * @param sourceType
	 * @param targetType
	 * @return
	 * @throws E
	 */
	default T convert(@Nullable S source, @Nullable Class<? extends S> sourceType, TypeDescriptor targetType) throws E {
		return convert(source, TypeDescriptor.valueOf(sourceType), targetType);
	}

	/**
	 * 默认无需实现，直接调用{@link #convert(Object, TypeDescriptor, TypeDescriptor)}
	 * 
	 * @see #convert(Object, TypeDescriptor, TypeDescriptor)
	 * @param source
	 * @param targetType
	 * @return
	 * @throws E
	 */
	default T convert(@Nullable S source, TypeDescriptor targetType) throws E {
		return convert(source, TypeDescriptor.forObject(source), targetType);
	}

	/**
	 * 默认无需实现，直接调用{@link #convert(Object, TypeDescriptor, TypeDescriptor)}
	 * 
	 * @see #convert(Object, TypeDescriptor, TypeDescriptor)
	 * @param <R>
	 * @param source
	 * @param sourceType
	 * @param targetType
	 * @return
	 * @throws E
	 */
	@SuppressWarnings("unchecked")
	default <R extends T> R convert(@Nullable S source, @Nullable TypeDescriptor sourceType,
			Class<? extends R> targetType) throws E {
		return (R) convert(source, sourceType, TypeDescriptor.valueOf(targetType));
	}

	T convert(@Nullable S source, @Nullable TypeDescriptor sourceType, TypeDescriptor targetType) throws E;
}
