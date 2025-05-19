package run.soeasy.framework.core.transform.service;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.transform.Transformer;

public interface ReversibleTransformer<S, T> extends Transformer<S, T> {
	boolean reverseTransform(@NonNull T source, @NonNull TypeDescriptor sourceType, S target,
			@NonNull TypeDescriptor targetType) throws ConversionException;

	default ReversibleTransformer<T, S> reversed() {
		return new ReversedTransformer<>(this);
	}
}
