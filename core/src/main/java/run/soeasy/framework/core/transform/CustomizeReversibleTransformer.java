package run.soeasy.framework.core.transform;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;

@AllArgsConstructor
public class CustomizeReversibleTransformer<S, T> implements ReversibleTransformer<S, T> {
	private Transformer<? super S, ? super T> transformer;
	private Transformer<? super T, ? super S> reversedTransformer;

	@Override
	public boolean transform(@NonNull S source, @NonNull TypeDescriptor sourceTypeDescriptor, @NonNull T target,
			@NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		return transformer.transform(source, sourceTypeDescriptor, target, targetTypeDescriptor);
	}

	@Override
	public boolean reverseTransform(@NonNull T source, @NonNull TypeDescriptor sourceTypeDescriptor, S target,
			@NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		return reversedTransformer.transform(source, sourceTypeDescriptor, target, targetTypeDescriptor);
	}

	@Override
	public ReversibleTransformer<T, S> reversed() {
		return new CustomizeReversibleTransformer<>(reversedTransformer, transformer);
	}
}
