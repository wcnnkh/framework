package run.soeasy.framework.core.transform;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;

public class ReversedTransformer<S, T> extends CustomizeReversibleTransformer<S, T> {
	private ReversibleTransformer<T, S> reversibleTransformer;

	public ReversedTransformer(ReversibleTransformer<T, S> reversibleTransformer) {
		super(null, null);
		this.reversibleTransformer = reversibleTransformer;
	}

	public ReversedTransformer(Transformer<? super S, ? super T> transformer,
			Transformer<? super T, ? super S> reversedTransformer) {
		super(transformer, reversedTransformer);
	}

	@Override
	public ReversibleTransformer<T, S> reversed() {
		return reversibleTransformer == null ? super.reversed() : reversibleTransformer;
	}

	@Override
	public boolean transform(@NonNull S source, @NonNull TypeDescriptor sourceTypeDescriptor, @NonNull T target,
			@NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		if (reversibleTransformer != null) {
			return reversibleTransformer.reverseTransform(source, sourceTypeDescriptor, target, targetTypeDescriptor);
		}
		return super.transform(source, sourceTypeDescriptor, target, targetTypeDescriptor);
	}

	@Override
	public boolean reverseTransform(@NonNull T source, @NonNull TypeDescriptor sourceType, S target,
			@NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		if (reversibleTransformer != null) {
			return reversibleTransformer.transform(source, targetTypeDescriptor, target, targetTypeDescriptor);
		}
		return super.reverseTransform(source, targetTypeDescriptor, target, targetTypeDescriptor);
	}
}
