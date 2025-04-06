package run.soeasy.framework.core.transform.mapping;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionFailedException;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.config.Converters;
import run.soeasy.framework.core.transform.stereotype.Accessor;
import run.soeasy.framework.core.transform.stereotype.ObjectTransformer;
import run.soeasy.framework.core.transform.stereotype.Template;

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
