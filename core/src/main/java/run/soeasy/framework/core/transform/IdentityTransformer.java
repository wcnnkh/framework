package run.soeasy.framework.core.transform;

import lombok.NonNull;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.type.InstanceFactory;
import run.soeasy.framework.core.type.InstanceFactorySupporteds;
import run.soeasy.framework.core.type.ResolvableType;

public interface IdentityTransformer<T> extends Converter<T, T>, Transformer<T, T>, InstanceFactory {
	@Override
	default boolean canInstantiated(@NonNull ResolvableType requiredType) {
		return InstanceFactorySupporteds.REFLECTION.canInstantiated(requiredType);
	}

	@Override
	default Object newInstance(@NonNull ResolvableType requiredType) {
		return InstanceFactorySupporteds.REFLECTION.newInstance(requiredType);
	}
}
