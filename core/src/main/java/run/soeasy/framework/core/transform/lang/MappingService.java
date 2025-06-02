package run.soeasy.framework.core.transform.lang;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ConverterRegistry;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.transform.Mapping;
import run.soeasy.framework.core.transform.ObjectMapper;
import run.soeasy.framework.core.transform.TransformerRegistry;

public class MappingService<K, V extends TypedValueAccessor, T extends Mapping<K, V>> extends ObjectMapper<K, V, T> {
	private final ConverterRegistry converterRegistry = new ConverterRegistry();
	private final TransformerRegistry transformerRegistry = new TransformerRegistry();
	private final CollectionTransformer collectionTransformer = new CollectionTransformer(this);
	private final MapTransformer mapTransformer = new MapTransformer(this, this);

	@Override
	public boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return super.canConvert(sourceTypeDescriptor, targetTypeDescriptor)
				|| converterRegistry.canConvert(sourceTypeDescriptor, targetTypeDescriptor);
	}

	@Override
	public boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return super.canTransform(sourceTypeDescriptor, targetTypeDescriptor)
				|| transformerRegistry.canTransform(sourceTypeDescriptor, targetTypeDescriptor);
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor)
			throws ConversionException {
		if (super.canConvert(sourceTypeDescriptor, targetTypeDescriptor)) {
			return super.convert(source, sourceTypeDescriptor, targetTypeDescriptor);
		}
		return converterRegistry.convert(source, sourceTypeDescriptor, targetTypeDescriptor);
	}

	@Override
	public boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull Object target, @NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		if (super.canTransform(sourceTypeDescriptor, targetTypeDescriptor)) {
			return super.transform(source, sourceTypeDescriptor, target, targetTypeDescriptor);
		}
		return transformerRegistry.transform(source, sourceTypeDescriptor, target, targetTypeDescriptor);
	}
}
