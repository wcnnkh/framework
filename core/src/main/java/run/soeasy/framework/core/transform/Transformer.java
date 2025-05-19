package run.soeasy.framework.core.transform;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;

@FunctionalInterface
public interface Transformer<S, T> {
	/**
	 * 传输
	 * 
	 * @param source
	 * @param sourceType
	 * @param target
	 * @param targetType
	 * @return
	 * @throws ConversionException
	 */
	boolean transform(@NonNull S source, @NonNull TypeDescriptor sourceType, @NonNull T target,
			@NonNull TypeDescriptor targetType) throws ConversionException;
}
