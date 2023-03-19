package io.basc.framework.convert;

/**
 * {@link Transformer}的相反行为，和{@link Transformer}的行为一致，定义的原因是为了方便同时存在的情况
 * 
 * @author wcnnkh
 *
 * @param <S> 来源
 * @param <T> 目标
 * @param <E> 异常
 */
public interface ReverseTransformer<S, T, E extends Throwable> {
	default void reverseTransform(S source, Class<? extends S> sourceType, T target) throws E {
		reverseTransform(source, sourceType, target, TypeDescriptor.forObject(target));
	}

	default void reverseTransform(S source, Class<? extends S> sourceType, T target, Class<? extends T> targetType)
			throws E {
		reverseTransform(source, TypeDescriptor.valueOf(sourceType), target, targetType);
	}

	default void reverseTransform(S source, Class<? extends S> sourceType, T target, TypeDescriptor targetType)
			throws E {
		reverseTransform(source, TypeDescriptor.valueOf(sourceType), target, targetType);
	}

	default void reverseTransform(S source, T target) throws E {
		reverseTransform(source, TypeDescriptor.forObject(source), target, TypeDescriptor.forObject(target));
	}

	default void reverseTransform(S source, T target, Class<? extends T> targetType) throws E {
		reverseTransform(source, TypeDescriptor.forObject(source), target, targetType);
	}

	default void reverseTransform(S source, T target, TypeDescriptor targetType) throws E {
		reverseTransform(source, TypeDescriptor.forObject(source), target, targetType);
	}

	default void reverseTransform(S source, TypeDescriptor sourceType, T target) throws E {
		reverseTransform(source, sourceType, target, TypeDescriptor.forObject(target));
	}

	default void reverseTransform(S source, TypeDescriptor sourceType, T target, Class<? extends T> targetType)
			throws E {
		reverseTransform(source, sourceType, target, TypeDescriptor.valueOf(targetType));
	}

	void reverseTransform(S source, TypeDescriptor sourceType, T target, TypeDescriptor targetType) throws E;
}