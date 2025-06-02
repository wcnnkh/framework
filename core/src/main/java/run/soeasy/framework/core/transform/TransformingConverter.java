package run.soeasy.framework.core.transform;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.ConverterNotFoundException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.type.InstanceFactory;
import run.soeasy.framework.core.type.InstanceFactorySupporteds;

@Getter
@Setter
public class TransformingConverter<S, T> implements Converter<S, T>, Transformer<S, T> {
	@NonNull
	private InstanceFactory instanceFactory = InstanceFactorySupporteds.REFLECTION;
	private Transformer<? super S, ? super T> transformer;
	private boolean enable = true;

	@Override
	public boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return enable && instanceFactory.canInstantiated(targetTypeDescriptor.getResolvableType())
				&& canTransform(sourceTypeDescriptor, targetTypeDescriptor);
	}

	@Override
	public boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return enable && transformer != null && transformer.canTransform(sourceTypeDescriptor, targetTypeDescriptor);
	}

	@Override
	public boolean transform(@NonNull S source, @NonNull TypeDescriptor sourceTypeDescriptor, @NonNull T target,
			@NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		if (!canTransform(sourceTypeDescriptor, targetTypeDescriptor)) {
			return false;
		}

		return transformer.transform(source, sourceTypeDescriptor, target, targetTypeDescriptor);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T convert(S source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		if (!canConvert(sourceTypeDescriptor, targetTypeDescriptor)) {
			throw new ConverterNotFoundException(sourceTypeDescriptor, targetTypeDescriptor);
		}

		T target = (T) instanceFactory.newInstance(targetTypeDescriptor.getResolvableType());
		transform(source, sourceTypeDescriptor, target, targetTypeDescriptor);
		return target;
	}
}
