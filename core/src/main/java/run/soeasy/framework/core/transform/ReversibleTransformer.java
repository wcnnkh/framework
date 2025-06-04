package run.soeasy.framework.core.transform;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;

public interface ReversibleTransformer<S, T> extends Transformer<S, T> {
	boolean reverseTransform(@NonNull T source, @NonNull TypeDescriptor sourceTypeDescriptor, S target,
			@NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException;

	default ReversibleTransformer<T, S> reversed() {
		return new ReversedTransformer<>(this);
	}
}
