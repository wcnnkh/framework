package io.basc.framework.util.function;

import lombok.NonNull;
import java.util.Optional;

/**
 * 对一个对象进行包装
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
@FunctionalInterface
public interface Wrapper<T> {

	public static boolean isWrapperFor(Object source, @NonNull Class<?> requiredType) {
		if (source == null) {
			return false;
		}

		if (requiredType.isInstance(source)) {
			return true;
		}

		if (source instanceof Wrapper) {
			return ((Wrapper<?>) source).isWrapperFor(requiredType);
		}
		return false;
	}

	public static <T> T unwrap(Object source, @NonNull Class<? extends T> requiredType) {
		if (source == null) {
			return null;
		}

		if (requiredType.isInstance(source)) {
			return requiredType.cast(source);
		}

		if (source instanceof Wrapper) {
			Wrapper<?> wrapper = (Wrapper<?>) source;
			return wrapper.unwrap(requiredType);
		}
		return null;
	}

	/**
	 * 被包装的来源
	 * 
	 * @return
	 */
	T getSource();

	/**
	 * 是否包装了此类型
	 * 
	 * @param requiredType
	 * @return
	 */
	default boolean isWrapperFor(@NonNull Class<?> requiredType) {
		if (requiredType.isInstance(this)) {
			return true;
		}

		T source = getSource();
		if (requiredType.isInstance(source)) {
			return true;
		}

		if (source instanceof Wrapper) {
			return ((Wrapper<?>) source).isWrapperFor(requiredType);
		}
		return false;
	}

	/**
	 * 获取解除包装后指定类型的对象
	 * 
	 * @param <S>
	 * @param requiredType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	default <S> S unwrap(@NonNull Class<? extends S> requiredType) {
		if (requiredType.isInstance(this)) {
			return (S) this;
		}

		T source = getSource();
		if (requiredType.isInstance(source)) {
			return (S) source;
		}

		if (source instanceof Wrapper) {
			return ((Wrapper<T>) source).unwrap(requiredType);
		}

		throw new IllegalArgumentException("Cannot unwrap for " + requiredType);
	}
}
