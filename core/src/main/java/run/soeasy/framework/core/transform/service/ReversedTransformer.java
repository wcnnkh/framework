package run.soeasy.framework.core.transform.service;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;

@RequiredArgsConstructor
@EqualsAndHashCode(of = "source")
class ReversedTransformer<S, T, W extends ReversibleTransformer<T, S>> implements ReversibleTransformer<S, T> {
	@NonNull
	private final W source;

	@Override
	public boolean transform(@NonNull S source, @NonNull TypeDescriptor sourceType, @NonNull T target,
			@NonNull TypeDescriptor targetType) throws ConversionException {
		return this.source.reverseTransform(source, sourceType, target, targetType);
	}

	@Override
	public boolean reverseTransform(@NonNull T source, @NonNull TypeDescriptor sourceType, S target,
			@NonNull TypeDescriptor targetType) throws ConversionException {
		return this.source.transform(source, sourceType, target, targetType);
	}

	@Override
	public ReversibleTransformer<T, S> reversed() {
		return source;
	}

	@Override
	public String toString() {
		return source.toString();
	}
}
