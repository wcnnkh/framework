package run.soeasy.framework.core.transform.templates;

import java.util.Collections;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.spi.ConfigurableServices;
import run.soeasy.framework.core.transform.Transformer;
import run.soeasy.framework.core.type.InstanceFactory;
import run.soeasy.framework.core.type.InstanceFactorySupporteds;
import run.soeasy.framework.core.type.ResolvableType;

@Getter
@Setter
public class DefaultMapper<K, V extends TypedValueAccessor, T extends Mapping<K, V>> extends GenericMapper<K, V, T>
		implements Transformer, Converter, InstanceFactory, MappingFactory<Object, K, V, T> {
	@NonNull
	private InstanceFactory instanceFactory = InstanceFactorySupporteds.REFLECTION;
	private final MappingProvider<K, V, T> mappingProvider = new MappingProvider<>();

	public DefaultMapper() {
		super(new ConfigurableServices<>(), new ValueMapper<>());
	}

	@Override
	public @NonNull ConfigurableServices<MappingFilter<K, V, T>> getFilters() {
		return (ConfigurableServices<MappingFilter<K, V, T>>) super.getFilters();
	}

	@Override
	public @NonNull ValueMapper<K, V, T> getMapper() {
		return (ValueMapper<K, V, T>) super.getMapper();
	}

	@Override
	public boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return (canInstantiated(targetTypeDescriptor.getResolvableType())
				&& canTransform(sourceTypeDescriptor, targetTypeDescriptor));
	}

	@Override
	public boolean canInstantiated(@NonNull ResolvableType requiredType) {
		return instanceFactory.canInstantiated(requiredType);
	}

	@Override
	public boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return (hasMapping(sourceTypeDescriptor) && hasMapping(targetTypeDescriptor));
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor)
			throws ConversionException {
		Object target = newInstance(targetTypeDescriptor.getResolvableType());
		transform(source, sourceTypeDescriptor, target, targetTypeDescriptor);
		return target;
	}

	@Override
	public T getMapping(@NonNull Object source, @NonNull TypeDescriptor requiredType) {
		return mappingProvider.getMapping(source, requiredType);
	}

	@Override
	public boolean hasMapping(@NonNull TypeDescriptor requiredType) {
		return mappingProvider.hasMapping(requiredType);
	}

	@Override
	public Object newInstance(@NonNull ResolvableType requiredType) {
		return instanceFactory.newInstance(requiredType);
	}

	@Override
	public boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull Object target, @NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		return transform(source, sourceTypeDescriptor, target, targetTypeDescriptor, Collections.emptyList());
	}

	public boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull Object target, @NonNull TypeDescriptor targetTypeDescriptor,
			@NonNull Iterable<MappingFilter<K, V, T>> filters) throws ConversionException {
		if (hasMapping(sourceTypeDescriptor) && hasMapping(targetTypeDescriptor)) {
			return doMapping(getMapping(source, sourceTypeDescriptor), getMapping(target, targetTypeDescriptor),
					filters);
		}
		return false;
	}

	public boolean doMapping(@NonNull T sourceMapping, @NonNull T targetMapping,
			@NonNull Iterable<MappingFilter<K, V, T>> filters) throws ConversionException {
		return doMapping(new MappingContext<>(sourceMapping), new MappingContext<>(targetMapping), filters);
	}

	// ------------------------------------以下都是为了继承时方便重写为final方法----------------------------------------------------//

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

	@Override
	public final boolean canTransform(@NonNull Class<?> sourceClass, @NonNull Class<?> targetClass) {
		return Transformer.super.canTransform(sourceClass, targetClass);
	}

	@Override
	public final boolean canTransform(@NonNull Class<?> sourceClass, @NonNull TypeDescriptor targetTypeDescriptor) {
		return Transformer.super.canTransform(sourceClass, targetTypeDescriptor);
	}

	@Override
	public final boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor, @NonNull Class<?> targetClass) {
		return Transformer.super.canTransform(sourceTypeDescriptor, targetClass);
	}

	@Override
	public final <U> U convert(@NonNull Object source, @NonNull Class<? extends U> targetClass)
			throws ConversionException {
		return Converter.super.convert(source, targetClass);
	}

	@Override
	public final Object convert(Object source, @NonNull Class<?> sourceClass,
			@NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		return Converter.super.convert(source, sourceClass, targetTypeDescriptor);
	}

	@Override
	public final <U> U convert(Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull Class<? extends U> targetClass) throws ConversionException {
		return Converter.super.convert(source, sourceTypeDescriptor, targetClass);
	}

	@Override
	public final Object convert(Object source, @NonNull TypeDescriptor targetTypeDescriptor)
			throws ConversionException {
		return Converter.super.convert(source, targetTypeDescriptor);
	}

	@Override
	public final boolean transform(@NonNull Object source, @NonNull Object target) {
		return Transformer.super.transform(source, target);
	}

	@Override
	public final boolean transform(@NonNull Object source, @NonNull Object target,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return Transformer.super.transform(source, target, targetTypeDescriptor);
	}

	@Override
	public final boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull Object target) {
		return Transformer.super.transform(source, sourceTypeDescriptor, target);
	}

	public final <U> boolean transform(Object source, TypeDescriptor sourceTypeDescriptor, U target,
			Class<? extends U> targetClass) {
		return Transformer.super.transform(source, sourceTypeDescriptor, target, targetClass);
	}

	public final <S, U> boolean transform(S source, Class<? extends S> sourceClass, U target,
			Class<? extends U> targetClass) {
		return Transformer.super.transform(source, sourceClass, target, targetClass);
	};

	public final <U> boolean transform(Object source, U target, Class<? extends U> targetClass) {
		return Transformer.super.transform(source, target, targetClass);
	};

	@Override
	public final <S> boolean transform(@NonNull S source, @NonNull Class<? extends S> sourceClass,
			@NonNull Object target) {
		return Transformer.super.transform(source, sourceClass, target);
	}

	@Override
	public final <S> boolean transform(@NonNull S source, @NonNull Class<? extends S> sourceClass,
			@NonNull Object target, @NonNull TypeDescriptor targetTypeDescriptor) {
		return Transformer.super.transform(source, sourceClass, target, targetTypeDescriptor);
	}
}
