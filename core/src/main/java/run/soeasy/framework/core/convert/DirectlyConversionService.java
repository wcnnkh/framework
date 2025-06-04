package run.soeasy.framework.core.convert;

import lombok.NonNull;

class DirectlyConversionService extends IdentityConversionService {
	static final DirectlyConversionService INSTANCE = new DirectlyConversionService();

	@Override
	public boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		if (super.canConvert(sourceTypeDescriptor, targetTypeDescriptor)) {
			return true;
		}

		if (targetTypeDescriptor.getType() == Object.class) {
			return true;
		}
		return false;
	}

}
