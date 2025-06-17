package run.soeasy.framework.core.convert.support;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.ConverterAware;
import run.soeasy.framework.core.convert.TypeDescriptor;

@Getter
@Setter
public abstract class AbstractConverter implements Converter, ConverterAware {
	@NonNull
	private Converter converter = Converter.assignable();

	public abstract boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor);

	@Override
	public final <T> T convert(@NonNull Object source, @NonNull Class<? extends T> targetClass)
			throws ConversionException {
		return Converter.super.convert(source, targetClass);
	}

	@Override
	public final Object convert(Object source, @NonNull Class<?> sourceClass,
			@NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		return Converter.super.convert(source, sourceClass, targetTypeDescriptor);
	}

	@Override
	public final <T> T convert(Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull Class<? extends T> targetClass) throws ConversionException {
		return Converter.super.convert(source, sourceTypeDescriptor, targetClass);
	}

	@Override
	public final Object convert(Object source, @NonNull TypeDescriptor targetTypeDescriptor)
			throws ConversionException {
		return Converter.super.convert(source, targetTypeDescriptor);
	}

	@Override
	public final boolean canConvert(@NonNull Class<?> sourceClass, @NonNull Class<?> targetClass) {
		return Converter.super.canConvert(sourceClass, targetClass);
	}

	@Override
	public final boolean canConvert(@NonNull Class<?> sourceClass, @NonNull TypeDescriptor targetTypeDescriptor) {
		return Converter.super.canConvert(sourceClass, targetTypeDescriptor);
	}

	@Override
	public final boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor, @NonNull Class<?> targetClass) {
		return Converter.super.canConvert(sourceTypeDescriptor, targetClass);
	}
}