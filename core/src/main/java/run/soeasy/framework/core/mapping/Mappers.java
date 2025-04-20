package run.soeasy.framework.core.mapping;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionFailedException;
import run.soeasy.framework.core.convert.ConverterNotFoundException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.spi.ConfigurableServices;

public class Mappers<S, T, E extends Throwable, M extends Mapper<? super S, T, ? extends E>>
		extends ConfigurableServices<M> implements Mapper<S, T, E> {
	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		for (M mapper : this) {
			if (mapper.canConvert(sourceType, targetType)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canInstantiated(@NonNull TypeDescriptor requiredType) {
		for (M mapper : this) {
			if (mapper.canInstantiated(requiredType)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canTransform(@NonNull TypeDescriptor sourceType, @NonNull TypeDescriptor targetType) {
		for (M mapper : this) {
			if (mapper.canTransform(sourceType, targetType)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public T convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws E, ConversionFailedException {
		for (M mapper : this) {
			if (mapper.canConvert(sourceType, targetType)) {
				return mapper.convert(source, sourceType, targetType);
			}
		}
		throw new ConverterNotFoundException(sourceType, targetType);
	}

	@Override
	public Object newInstance(@NonNull TypeDescriptor requiredType) {
		for (M mapper : this) {
			if (mapper.canInstantiated(requiredType)) {
				return mapper.newInstance(requiredType);
			}
		}
		throw new UnsupportedOperationException(requiredType.toString());
	}

	@Override
	public void transform(@NonNull S source, @NonNull TypeDescriptor sourceType, @NonNull T target,
			@NonNull TypeDescriptor targetType) throws E {
		for (M mapper : this) {
			if (mapper.canTransform(sourceType, targetType)) {
				mapper.transform(source, sourceType, target, targetType);
				return;
			}
		}
	}
}
