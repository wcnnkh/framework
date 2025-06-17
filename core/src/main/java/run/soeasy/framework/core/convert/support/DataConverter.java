package run.soeasy.framework.core.convert.support;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.codec.DecodeException;
import run.soeasy.framework.codec.EncodeException;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ConversionFailedException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.TypeMapping;
import run.soeasy.framework.core.convert.value.TypedData;
import run.soeasy.framework.core.convert.value.TypedValue;

@Getter
@RequiredArgsConstructor
public class DataConverter<S, T> extends AbstractConditionalConverter implements Codec<TypedData<S>, TypedData<T>> {
	@NonNull
	private final Class<S> sourceType;
	@NonNull
	private final Class<T> targetType;

	@Override
	public final Set<TypeMapping> getConvertibleTypeMappings() {
		Set<TypeMapping> typeMappings = new HashSet<>(2, 1);
		TypeMapping typeMapping = new TypeMapping(sourceType, targetType);
		typeMappings.add(typeMapping);
		typeMappings.add(typeMapping.reversed());
		return typeMappings;
	}

	@Override
	public final boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return super.canConvert(sourceTypeDescriptor, targetTypeDescriptor);
	}

	@Override
	public final Object convert(Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		TypedValue value = TypedValue.of(source, targetTypeDescriptor);
		TypeMapping typeMapping = new TypeMapping(sourceType, targetType);
		if (typeMapping.canConvert(sourceTypeDescriptor, targetTypeDescriptor)) {
			try {
				TypedData<T> target = encode(value.getAsData(sourceTypeDescriptor));
				return target.value().getAsObject(targetTypeDescriptor);
			} catch (EncodeException e) {
				throw new ConversionFailedException(sourceTypeDescriptor, targetTypeDescriptor, source, e);
			}
		} else if (typeMapping.canConvert(targetTypeDescriptor, sourceTypeDescriptor)) {
			try {
				TypedData<S> target = decode(value.getAsData(sourceTypeDescriptor));
				return target.value().getAsObject(targetTypeDescriptor);
			} catch (DecodeException e) {
				throw new ConversionFailedException(sourceTypeDescriptor, targetTypeDescriptor, source, e);
			}
		}
		throw new ConversionFailedException(sourceTypeDescriptor, targetTypeDescriptor, source, null);
	}

	@Override
	public TypedData<T> encode(TypedData<S> source) throws EncodeException {
		return source.value().getAsData(targetType);
	}

	@Override
	public TypedData<S> decode(TypedData<T> source) throws DecodeException {
		return source.value().getAsData(sourceType);
	}
}
