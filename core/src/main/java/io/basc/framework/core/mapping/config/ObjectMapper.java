package io.basc.framework.core.mapping.config;

import io.basc.framework.core.convert.ConversionFailedException;
import io.basc.framework.core.convert.Converter;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.config.Converters;
import io.basc.framework.core.convert.transform.Accessor;
import io.basc.framework.core.convert.transform.Template;
import io.basc.framework.core.convert.transform.stractegy.ObjectTransformer;
import io.basc.framework.core.mapping.Mapper;
import lombok.NonNull;

public class ObjectMapper<K, V extends Accessor, T extends Template<K, ? extends V>, E extends Throwable>
		extends ObjectTransformer<K, V, T, E> implements Mapper<Object, Object, E> {
	private final ConfigurableInstanceFactory instanceFactory = new ConfigurableInstanceFactory();
	private final Converters<Object, Object, E, Converter<? super Object, ? extends Object, ? extends E>> converters = new Converters<>();

	public ConfigurableInstanceFactory getInstanceFactory() {
		return instanceFactory;
	}

	public Converters<Object, Object, E, Converter<? super Object, ? extends Object, ? extends E>> getConverters() {
		return converters;
	}

	@Override
	public boolean canInstantiated(@NonNull TypeDescriptor requiredType) {
		return instanceFactory.canInstantiated(requiredType);
	}

	@Override
	public Object newInstance(@NonNull TypeDescriptor requiredType) {
		return instanceFactory.canInstantiated(requiredType);
	}

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return converters.canConvert(sourceType, targetType) || Mapper.super.canConvert(sourceType, targetType);
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws E, ConversionFailedException {
		if (converters.canConvert(sourceType, targetType)) {
			return converters.convert(source, sourceType, targetType);
		}
		return Mapper.super.convert(source, sourceType, targetType);
	}

	@SuppressWarnings("unchecked")
	public <R> R convert(Object source, Class<? extends R> targetType) throws ConversionFailedException, E {
		return (R) convert(source, TypeDescriptor.forObject(source), TypeDescriptor.valueOf(targetType));
	}
}
