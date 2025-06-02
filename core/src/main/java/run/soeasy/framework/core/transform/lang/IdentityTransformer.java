package run.soeasy.framework.core.transform.lang;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.transform.Transformer;
import run.soeasy.framework.core.type.InstanceFactory;
import run.soeasy.framework.core.type.InstanceFactorySupporteds;

@Getter
@Setter
public abstract class IdentityTransformer<T> implements Transformer<T, T>, Converter<T, T> {
	private boolean enable = true;
	@NonNull
	private InstanceFactory instanceFactory = InstanceFactorySupporteds.REFLECTION;

	@Override
	public boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return canTransform(sourceTypeDescriptor, targetTypeDescriptor)
				&& instanceFactory.canInstantiated(targetTypeDescriptor.getResolvableType());
	}

	@Override
	public boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return enable && sourceTypeDescriptor.getType() == targetTypeDescriptor.getType();
	}

}
