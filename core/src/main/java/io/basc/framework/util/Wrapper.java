package io.basc.framework.util;

import lombok.NonNull;

/**
 * 对一个对象进行包装
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public interface Wrapper<T> {

	/**
	 * 被包装的来源
	 * 
	 * @return
	 */
	T getSource();

	/**
	 * 获取解除包装后指定类型的对象
	 * 
	 * @param <S>
	 * @param requiredType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	default <S> S unwrap(@NonNull Class<S> requiredType) {
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
}
