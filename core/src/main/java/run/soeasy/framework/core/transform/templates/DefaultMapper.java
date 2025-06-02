package run.soeasy.framework.core.transform.templates;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ConversionService;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.spi.ConfigurableServices;
import run.soeasy.framework.core.transform.TransformationService;
import run.soeasy.framework.core.type.InstanceFactory;
import run.soeasy.framework.core.type.InstanceFactorySupporteds;
import run.soeasy.framework.core.type.ResolvableType;

@Getter
@Setter
public class DefaultMapper<K, V extends TypedValueAccessor, T extends Mapping<K, V>> extends GenericMapper<K, V, T>
		implements TransformationService, ConversionService, InstanceFactory, MappingProvider<Object, K, V, T> {
	@NonNull
	private InstanceFactory instanceFactory = InstanceFactorySupporteds.REFLECTION;
	private final MappingRegistry<K, V, T> mappingRegistry = new MappingRegistry<>();

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
		return mappingRegistry.getMapping(source, requiredType);
	}

	@Override
	public boolean hasMapping(@NonNull TypeDescriptor requiredType) {
		return mappingRegistry.hasMapping(requiredType);
	}

	@Override
	public Object newInstance(@NonNull ResolvableType requiredType) {
		return instanceFactory.newInstance(requiredType);
	}

	@Override
	public boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull Object target, @NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		T sourceMapping = getMapping(source, sourceTypeDescriptor);
		T targetMapping = getMapping(target, targetTypeDescriptor);
		return doMapping(new MappingContext<>(sourceMapping), new MappingContext<>(targetMapping));
	}
}
