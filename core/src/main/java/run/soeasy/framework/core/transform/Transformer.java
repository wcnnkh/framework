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
	 * @param sourceTypeDescriptor
	 * @param target
	 * @param targetTypeDescriptor
	 * @return
	 * @throws ConversionException
	 */
	boolean transform(@NonNull S source, @NonNull TypeDescriptor sourceTypeDescriptor, @NonNull T target,
			@NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException;
}
