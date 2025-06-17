package run.soeasy.framework.core.convert;

import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

import lombok.NonNull;

public class FunctionConverter<S, T> implements ConditionalConverter {
	private final TypeMapping typeMapping;
	private final Function<? super S, ? extends T> function;

	public FunctionConverter(@NonNull Class<S> sourceType, @NonNull Class<T> targetType,
			@NonNull Function<? super S, ? extends T> function) {
		this.typeMapping = new TypeMapping(sourceType, targetType);
		this.function = function;
	}

	@Override
	public Set<TypeMapping> getConvertibleTypeMappings() {
		return Collections.singleton(typeMapping);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object convert(Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		return function.apply((S) source);
	}
}
